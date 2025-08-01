package ro.neforii.service;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.Event;
import ro.neforii.dto.CommentListResponseDto;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.update.CommentUpdateRequestDto;
import ro.neforii.dto.user.UserResponseDto;
import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.mapper.CommentMapper;
import ro.neforii.mapper.UserMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.UserRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

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
                .map(comment -> buildComment(comment, currentUser))
                .toList();
    }


    public Comment getComment(UUID id) {
        return commentRepo.findById(id).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Comment with id=" + id + " not found in getComment()");
            return new CommentNotFoundException("Comment with id " + id + " does not exist");
        });
    }

    public CommentResponseDto createCommentOnPost(String content, User user, UUID postId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Attempt to comment on nonexistent post id=" + postId);
            return new PostNotFoundException("Post with id=" + postId + " not found");
        });

        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        commentRepo.save(comment);
        Logger.log(LoggerType.INFO, "Created comment id=" + comment.getId() + " on post id=" + postId);

        CommentResponseDto commentResponseDto = buildComment(comment, user);
        return commentResponseDto;
    }

    public CommentResponseDto createReplyToComment(String content, User user, UUID parentCommentId) {
        Comment parent = commentRepo.findById(parentCommentId).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Attempt to reply to nonexistent comment id=" + parentCommentId);
            return new CommentNotFoundException("Could not find comment with id=" + parentCommentId);
        });

        Comment reply = Comment.builder()
                .content(content)
                .user(user)
                .parentComment(parent)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        commentRepo.save(reply);
        Logger.log(LoggerType.INFO, "Created reply id=" + reply.getId() + " to comment id=" + parentCommentId);
        CommentResponseDto commentResponseDto = buildComment(reply,user);
        return commentResponseDto;
    }

    public CommentResponseDto updateComment(UUID id, CommentUpdateRequestDto commentDto, UUID currentUserId) {
        Comment comment = commentRepo.findById(id).orElseThrow(() -> new CommentNotFoundException("The cooment with ID" + id + " not found." ));

        comment.setContent(commentDto.content());
        comment.setUpdatedAt(LocalDateTime.now());

        // Nu e o solutie finala, dar e una temporala
        UserResponseDto userDto = userService.findById(currentUserId);
        User user = userMapper.UserResponseToUser(userDto);

        try {
            Comment updatedComm = commentRepo.save(comment);
            Logger.log(LoggerType.INFO, "Comment updated successfully!");
            return buildComment(updatedComm, user);

        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to update comment id=" + id);
            throw new RuntimeException("Failed to update comment");
        }
    }

    public void deleteComment(UUID id, UUID currentUserId) {
        // Verif: daca userul care incearca sa stearga postarea este cel care a facut postarea
        getComment(id);
        try {
            commentRepo.deleteById(id);
            Logger.log(LoggerType.INFO, "Comment id=" + id + " deleted successfully");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to delete comment id=" + id);
            throw new RuntimeException("Failed to delete comment with id=" + id);
        }
    }


    public List<CommentResponseDto> getRepliesForComment(UUID commentId, UUID currentUserId) {
        getComment(commentId);

        // Nu e o solutie finala, dar e una temporala
        UserResponseDto userDto = userService.findById(currentUserId);
        User user = userMapper.UserResponseToUser(userDto);

        List <Comment> replies = commentRepo.findByParentCommentId(commentId);
        return replies.stream()
                .map(rply -> buildComment(rply,user))
                .collect(Collectors.toList());
    }

    public UUID findPostIdForComment(UUID commentId) {
        Optional<Comment> currentOptional = commentRepo.findById(commentId);

        while (currentOptional.isPresent()) {
            Comment current = currentOptional.get();
            if (current.getPost() != null) {
                return current.getPost().getId();
            }
            Comment parent = current.getParentComment();
            if (parent == null) break;
            currentOptional = commentRepo.findById(parent.getId());
        }

        throw new IllegalStateException("Could not trace postId for comment id=" + commentId);
    }



    public List<CommentResponseDto> getTopLevelComments(UUID postId, UUID currentUserId ) {
        UserResponseDto userDto = userService.findById(currentUserId);
        User user = userMapper.UserResponseToUser(userDto);

        return commentRepo.findByPostId(postId).stream()
                .filter(c -> c.getParentComment() == null)
                .map(comment -> buildComment(comment, user))
                .toList();
    }

    public int displayUpvotes(UUID id) {
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, true);
    }

    public int displayDownvotes(UUID id) {
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, false);
    }

    public CommentResponseDto buildComment(Comment comment, User currentUser) {
        if (comment == null) {
            return null;
        }

        int upVotes = displayUpvotes(comment.getId());
        int downVotes = displayDownvotes(comment.getId());
        int score = upVotes - downVotes;
        String userVote = voteService.getVoteTypeForUser(comment,currentUser);

        List<CommentResponseDto> repliesDto = Optional.ofNullable(comment.getReplies())
                .orElse(List.of()) // dacă e null -> listă goală
                .stream()
                .map(reply -> buildComment(reply, currentUser))
                .toList();


        return new CommentResponseDto(
                comment.getId(),
                comment.getPost() != null ? comment.getPost().getId() : null,
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent(),
                comment.getUser().getUsername(),
                upVotes,
                downVotes,
                score,
                userVote,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                repliesDto
        );
    }
    public CommentListResponseDto getCommentsForPost(UUID postId, UUID currentUserId) {
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

        return new CommentListResponseDto(topLevel, allComments.size());
    }

    public int countCommentsForPost(UUID postId) {
        return commentRepo.countByPostId(postId);
    }

}
