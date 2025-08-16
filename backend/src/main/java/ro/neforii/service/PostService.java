package ro.neforii.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import ro.neforii.client.ImageProcessorClient;
import ro.neforii.dto.CommentListResponseDto;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.create.CommentOnPostRequestDto;
import ro.neforii.dto.post.*;
import ro.neforii.dto.vote.VoteRequestDto;
import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.exception.FileStorageException;
import ro.neforii.exception.ForbiddenActionException;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.VoteType;
import ro.neforii.repository.PostRepository;
import ro.neforii.service.security.OwnershipValidator;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final VoteService voteService;
    private final UserService userService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final OwnershipValidator ownershipValidator;
    private final FileService fileService;
    private final ImageProcessorClient imageProcessorClient;
    private static final String LOG_PREFIX = "PostService: ";

    @Transactional
    public PostResponseDto createPost(PostRequestDto form, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Creating new post by user " + form.author());

        User user = userService.getUserByUsername(form.author());

        Post post = postMapper.postRequestDtoToPost(form, user);

        MultipartFile image = form.image();
        if (image != null && !image.isEmpty()) {
            String imageUrl = null;
            Integer filterId = form.filter();

            if (filterId != null) {
                try {
                    byte[] filteredBytes = imageProcessorClient.applyFilter(image, filterId);
                    try {
                        imageUrl = fileService.save(filteredBytes);
                    } catch (IOException uploadFilteredEx) {
                        throw new FileStorageException("Failed to upload filtered image", uploadFilteredEx);
                    }
                } catch (RestClientException | IllegalStateException | IOException filterEx) {
                    Logger.log(
                            LoggerType.ERROR,
                            LOG_PREFIX + "Filter failed (" + filterEx.getClass().getSimpleName() + "): " + filterEx.getMessage()
                    );
                    try {
                        imageUrl = fileService.save(image);
                    } catch (IOException uploadOrigEx) {
                        throw new FileStorageException("Failed to upload original image after filter failure", uploadOrigEx);
                    }
                }
            } else {
                try {
                    imageUrl = fileService.save(image);
                } catch (IOException uploadEx) {
                    throw new FileStorageException("Failed to upload image", uploadEx);
                }
            }

            post.setImageUrl(imageUrl);
        }

        Post saved = postRepository.save(post);
        Logger.log(LoggerType.INFO, LOG_PREFIX + "Created post with ID " + saved.getId());
        return postMapper.postToPostResponseDto(saved, user.getId());
    }


    public List<PostResponseDto> getAllPostsAsUser(UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Retrieving all posts for user " + currentUserId);
        var sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        var posts = postRepository.findAll(sort);
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Found " + posts.size() + " posts");

        return posts.stream()
                .map(post -> postMapper.postToPostResponseDto(post, currentUserId))
                .toList();
    }

    public PostResponseDto getPostByIdAsUser(UUID id, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Retrieving post with ID " + id);
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found."));
            return postMapper.postToPostResponseDto(post, currentUserId);
        } catch (PostNotFoundException e) {
            Logger.log(LoggerType.WARNING, LOG_PREFIX + e.getMessage());
            throw e;
        }
    }

    public PostResponseDto updatePost(UUID id, PostUpdateRequestDto postUpdateRequestDto, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Attempting to update post with ID " + id);
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> {
                        Logger.log(LoggerType.WARNING, LOG_PREFIX + "Post not found with ID: " + id);
                        return new PostNotFoundException("Post with ID " + id + " not found.");
                    });
            // ownershipValidator.assertPostOwner(currentUserId, post);

            // verifica ce este actualizat
            boolean titleUpdated = false;
            boolean contentUpdated = false;

            if (postUpdateRequestDto.title() != null) {
                post.setTitle(postUpdateRequestDto.title());
                titleUpdated = true;
            }
            if (postUpdateRequestDto.content() != null) {
                post.setContent(postUpdateRequestDto.content());
                contentUpdated = true;
            }
            post.setUpdatedAt(LocalDateTime.now());

            Post updatedPost = postRepository.save(post);

            Logger.log(LoggerType.INFO, LOG_PREFIX + "Successfully updated post " + id +
                    (titleUpdated ? " (title updated)" : "") +
                    (contentUpdated ? " (content updated)" : ""));

            return postMapper.postToPostResponseDto(updatedPost, post.getUser().getId());
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to update post " + id + ": " + e.getMessage());
            throw e;
        }
    }

    public void deletePost(UUID id, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Attempting to delete post with ID " + id);
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found."));

            if (!post.getUser().getId().equals(currentUserId)) {
                Logger.log(LoggerType.WARNING, LOG_PREFIX + "Unauthorized delete attempt of post " + id + " by user " + currentUserId);
                throw new ForbiddenActionException("The current user is not the author of the post.");
            }

            postRepository.delete(post);
            Logger.log(LoggerType.INFO, LOG_PREFIX + "Successfully deleted post with ID " + id);
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to delete post with ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

    public PostVoteResponseDto votePost(UUID postId, UUID currentUserId, VoteRequestDto voteRequestDto) {
        VoteType voteType = VoteType.fromString(voteRequestDto.voteType());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + postId + " not found."));

//        ownershipValidator.assertPostOwner(currentUserId, post);

        voteService.createOrUpdateVoteForPost(currentUserId, postId, voteType);

        Post updatedPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + postId + " not found."));

        return postMapper.postToPostVoteResponseDto(updatedPost, currentUserId);
    }

    public CommentListResponseDto getCommentsForPost(UUID id, UUID currentUserId) {
    Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Retrieving comments for post with ID " + id);
        return commentService.getCommentsForPost(id, currentUserId);
    }

    public CommentResponseDto createComment(UUID postId, CommentOnPostRequestDto request, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Creating new comment on post " + postId + " by author " + request.author());
        try {
            User user = userService.getUserByUsername(request.author());
            String username = user.getUsername();

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        Logger.log(LoggerType.WARNING, LOG_PREFIX + "Failed to create comment: Post not found with ID: " + postId);
                        return new PostNotFoundException("Post not found with ID: " + postId);
                    });

            Comment parent = null;
            if (request.parentId() != null) {
                Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Comment has parent ID: " + request.parentId());
                parent = commentService.findById(request.parentId())
                        .orElseThrow(() -> {
                            Logger.log(LoggerType.WARNING, LOG_PREFIX + "Failed to create comment: Parent comment not found with ID: " + request.parentId());
                            return new CommentNotFoundException("Parent comment not found with ID: " + request.parentId());
                        });
            }

            CommentResponseDto response = commentService.createCommentOnPost(request.content(), user, post, parent);
            Logger.log(LoggerType.INFO, LOG_PREFIX + "User '" + username + "' successfully created comment " +
                    response.id() + " on post " + postId + (parent != null ? " as reply to comment " + parent.getId() : ""));

            return response;
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to create comment on post " + postId + ": " + e.getMessage());
            throw e;
        }
    }

//    public PostResponseDto createImagePost(PostRequestDto postRequestDto, String imageUrl) {
//        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Creating new image post by user " + postRequestDto.author() + " with image path: " + imageUrl);
//        try {
//            User user = userService.getUserByUsername(postRequestDto.author());
//            Post post = postMapper.postRequestDtoToPost(postRequestDto, user);
//
//            post.setImagePath(imageUrl);
//
//            Post savedPost = postRepository.save(post);
//            Logger.log(LoggerType.INFO, LOG_PREFIX + "Created image post with ID " + savedPost.getId() + " for user " + user.getUsername());
//
//            return postMapper.postToPostResponseDto(savedPost, user.getId());
//        } catch (Exception e) {
//            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to create image post for user " + postRequestDto.author() + ": " + e.getMessage());
//            throw e;
//        }
//    }

}
