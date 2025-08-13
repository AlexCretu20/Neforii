package ro.neforii.service;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.neforii.dto.CommentListResponseDto;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.update.CommentUpdateRequestDto;
import ro.neforii.dto.comment.vote.CommentVoteRequestDto;
import ro.neforii.dto.comment.vote.CommentVoteResponseDto;
import ro.neforii.dto.post.PostCommentResponseDto;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.exception.BadRequestException;
import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.mapper.CommentMapper;
import ro.neforii.mapper.UserMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.VoteType;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.UserRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService implements IVotable {
    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final VoteRepository voteRepo;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final VoteService voteService;
    private static final String LOG_PREFIX = "CommentService: ";

    public CommentService(CommentRepository commentRepo, UserRepository userRepo, PostRepository postRepo, VoteRepository voteRepo, CommentMapper commentMapper, UserService userService, UserMapper userMapper, VoteService voteService) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.voteRepo = voteRepo;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.voteService = voteService;
    }

    public List<CommentResponseDto> getComments(User currentUser) {
        return commentRepo.findAll().stream()
                .map(comment -> commentMapper.commentToDto(comment, currentUser.getId()))
                .toList();
    }


    public CommentResponseDto getComment(UUID id, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Retrieving comment with ID " + id);
        try {
            Comment comment = commentRepo.findById(id).orElseThrow(() -> {
                Logger.log(LoggerType.WARNING, LOG_PREFIX + "Comment with id=" + id + " not found");
                return new CommentNotFoundException("Comment with id " + id + " does not exist");
            });

            return commentMapper.commentToDto(comment, currentUserId);
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error retrieving comment: " + e.getMessage());
            throw e;
        }
    }


    public CommentResponseDto createCommentOnPost(String content, User user, Post post, Comment parent) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Creating new comment on post " + post.getId() + " by user " + user.getUsername());
        try {
            Comment comment = Comment.builder()
                    .content(content)
                    .user(user)
                    .post(post)
                    .parentComment(parent)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Comment savedComment = commentRepo.save(comment);
            Logger.log(LoggerType.INFO, LOG_PREFIX + "Created comment with ID " + savedComment.getId());

            return commentMapper.commentToDto(savedComment, user.getId());
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to create comment: " + e.getMessage());
            throw e;
        }
    }

    public CommentResponseDto updateComment(UUID id, CommentUpdateRequestDto commentDto, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Updating comment with ID " + id);
        try {
            Comment comment = commentRepo.findById(id).orElseThrow(() -> {
                Logger.log(LoggerType.WARNING, LOG_PREFIX + "Comment with id " + id + " not found");
                return new CommentNotFoundException("The comment with ID " + id + " not found.");
            });

            comment.setContent(commentDto.content());
            comment.setUpdatedAt(LocalDateTime.now());

            Comment updatedComment = commentRepo.save(comment);
            Logger.log(LoggerType.INFO, LOG_PREFIX + "Comment " + id + " updated successfully");

            return commentMapper.commentToDto(updatedComment, currentUserId);
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to update comment " + id + ": " + e.getMessage());
            throw e;
        }
    }
    public void deleteComment(UUID id, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Attempting to delete comment with ID " + id);
        try {
            // Verif: daca userul care incearca sa stearga postarea este cel care a facut postarea
            getComment(id, currentUserId);
            commentRepo.deleteById(id);
            Logger.log(LoggerType.INFO, LOG_PREFIX + "Comment " + id + " deleted successfully");
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to delete comment " + id + ": " + e.getMessage());
            throw e;
        }
    }

    public CommentVoteResponseDto voteComm(UUID commentId, UUID currentUserId, CommentVoteRequestDto voteRequestDto) {
        String username = "unknown";
        try {
            User user = userRepo.findById(currentUserId).orElse(null);
            if (user != null) {
                username = user.getUsername();
            }

            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Processing vote request from user '" + username +
                    "' for comment " + commentId + " with vote type: " + voteRequestDto.voteType());

            VoteType voteType = VoteType.fromString(voteRequestDto.voteType());
            voteService.createOrUpdateVoteForComment(currentUserId, commentId, voteType);

            Comment updatedComm = commentRepo.findById(commentId)
                    .orElseThrow(() -> {
                        Logger.log(LoggerType.WARNING, LOG_PREFIX + "Comment with id " + commentId + " not found after voting");
                        return new CommentNotFoundException("Comment with id " + commentId + " not found.");
                    });

            Logger.log(LoggerType.INFO, LOG_PREFIX + "User '" + username + "' successfully " +
                    (voteType == VoteType.UP ? "upvoted" : (voteType == VoteType.DOWN ? "downvoted" : "removed vote from")) +
                    " comment " + commentId);

            return commentMapper.commentVoteResponseDto(updatedComm, currentUserId);
        } catch (IllegalArgumentException ex) {
            Logger.log(LoggerType.WARNING, LOG_PREFIX + "Invalid vote type from user '" + username +
                    "' for comment " + commentId + ": " + ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Error processing vote from user '" + username +
                    "' for comment " + commentId + ": " + e.getMessage());
            throw e;
        }
    }

    public List<CommentResponseDto> getTopLevelComments(UUID postId, UUID currentUserId) {
        UserResponseDto userDto = userService.findById(currentUserId);
        User user = userMapper.UserResponseToUser(userDto);

        return commentRepo.findByPostId(postId).stream()
                .filter(c -> c.getParentComment() == null)
                .map(comment -> commentMapper.commentToDto(comment, currentUserId))
                .toList();
    }

    public int displayUpvotes(UUID id) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Counting upvotes for comment with ID " + id);
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, true);
    }

    public int displayDownvotes(UUID id) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Counting downvotes for comment with ID " + id);
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, false);
    }

    @Transactional(readOnly = true) // optimizeaza, pentru citiri dificile din baza de date
    public CommentListResponseDto getCommentsForPost(UUID postId, UUID currentUserId) {
        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Retrieving comments for post with ID " + postId + " for user " + currentUserId);
        try {
            List<Comment> allComments = commentRepo.findByPostId(postId);

            Map<UUID, CommentResponseDto> dtoById = allComments.stream()
                    .map(c -> commentMapper.commentToDto(c, currentUserId))
                    .collect(Collectors.toMap(CommentResponseDto::id, dto -> dto));

            // build tree
            List<CommentResponseDto> topLevel = new ArrayList<>();
            for (Comment comment : allComments) {
                CommentResponseDto dto = dtoById.get(comment.getId());
                if (comment.getParentComment() != null) {
                    CommentResponseDto parentDto = dtoById.get(comment.getParentComment().getId());
                    if (parentDto != null) {
                        ((List<CommentResponseDto>) parentDto.replies()).add(dto);
                    }
                } else {
                    topLevel.add(dto);
                }
            }

            CommentListResponseDto result = new CommentListResponseDto(topLevel, allComments.size());
            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Retrieved " + topLevel.size() + " top-level comments (total: " + allComments.size() + ") for post " + postId);
            return result;
        } catch (Exception e) {
            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to retrieve comments for post " + postId + ": " + e.getMessage());
            throw e;
        }
    }

    public int countCommentsForPost(UUID postId) {
    Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Counting comments for post with ID " + postId);
        return commentRepo.countByPostId(postId);
    }

    public Optional<Comment> findById(UUID id) {
    Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Finding comment by ID " + id);
        return commentRepo.findById(id);
    }
}
