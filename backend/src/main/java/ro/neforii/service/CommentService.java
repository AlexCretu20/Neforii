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
        Comment comment = commentRepo.findById(id).orElseThrow(() -> {
            Logger.log(LoggerType.FATAL, "Comment with id=" + id + " not found in getComment()");
            return new CommentNotFoundException("Comment with id " + id + " does not exist");
        });

        return commentMapper.commentToDto(comment, currentUserId);
    }


    public CommentResponseDto createCommentOnPost(String content, User user, Post post, Comment parent) {
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .parentComment(parent)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepo.save(comment);

        return commentMapper.commentToDto(savedComment, user.getId());
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
        CommentResponseDto commentResponseDto = commentMapper.commentToDto(reply, parentCommentId);
        return commentResponseDto;
    }

    public CommentResponseDto updateComment(UUID id, CommentUpdateRequestDto commentDto, UUID currentUserId) {
        Comment comment = commentRepo.findById(id).orElseThrow(() -> new CommentNotFoundException("The cooment with ID" + id + " not found."));

        comment.setContent(commentDto.content());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComm;
        try {
            updatedComm = commentRepo.save(comment);
            Logger.log(LoggerType.INFO, "Comment updated successfully!");

        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to update comment id=" + id);
            throw new RuntimeException("Failed to update comment");
        }

        return commentMapper.commentToDto(updatedComm, currentUserId);
    }

    public void deleteComment(UUID id, UUID currentUserId) {
        // Verif: daca userul care incearca sa stearga postarea este cel care a facut postarea
        getComment(id, currentUserId);
        try {
            commentRepo.deleteById(id);
            Logger.log(LoggerType.INFO, "Comment id=" + id + " deleted successfully");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Failed to delete comment id=" + id);
            throw new RuntimeException("Failed to delete comment with id=" + id);
        }
    }

    public CommentVoteResponseDto voteComm(UUID commentId, UUID currentUserId, CommentVoteRequestDto voteRequestDto) {
        VoteType voteType;
        try {
            voteType = VoteType.fromString(voteRequestDto.voteType());
            voteService.createOrUpdateVoteForComment(currentUserId, commentId, voteType);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        Comment updatedComm = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id" + commentId + "not found."));

        return commentMapper.commentVoteResponseDto(updatedComm, currentUserId);

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
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, true);
    }

    public int displayDownvotes(UUID id) {
        Comment comment = commentRepo.findById(id).orElseThrow();
        return voteRepo.countByCommentAndIsUpvote(comment, false);
    }

    @Transactional(readOnly = true) // optimizeaza, pentru citiri dificile din baza de date
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

    public Optional<Comment> findById(UUID id) {
        return commentRepo.findById(id);
    }
}
