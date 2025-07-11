package service;

import model.*;

import java.time.LocalDateTime;
import java.util.*;

public class CommentService implements IVotable {
    private static CommentService instance;

    private int cnt = 1;
    private Map<Integer, Comment> comments = new HashMap<>();

    private CommentService() {
    }

    public synchronized static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }

    public void initService() {
        comments.clear();
        cnt = 1;
    }

    public Map<Integer, Comment> getComments() {
        return comments;
    }

    public Optional<Comment> getComment(int id) {
        return Optional.ofNullable(comments.get(id));
    }

    public void createComment(String text, User user, EntityType entityType, int entityId) {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment(cnt, text, now, now, user, entityType, entityId, new ArrayList<>(), new ArrayList<>());

        if (entityType == EntityType.POST) {
            Post post = PostService.getInstance().getPostById(entityId);
            if (post != null) {
                post.getComments().add(comment);
                comments.put(cnt, comment);
                System.out.println("Comment added to post.\n");
            } else {
                System.out.println("Post not found.\n");
                return;
            }

        } else if (entityType == EntityType.COMMENT) {
            Optional<Comment> parentOpt = getComment(entityId);
            if (parentOpt.isPresent()) {
                Comment parent = parentOpt.get();
                parent.getReplies().add(comment);
                comments.put(cnt, comment);
                System.out.println("Reply added to comment.\n");
            } else {
                System.out.println("Comment not found.\n");
                return;
            }
        }

        cnt++;
        System.out.println("The comment has been successfully added!\n");
    }

    public void updateComment(int id, String updateText) {
        Optional<Comment> opt = getComment(id);
        if (opt.isPresent()) {
            Comment comment = opt.get();
            comment.setText(updateText);
            comment.setUpdatedAt(LocalDateTime.now());
            System.out.println("Comment updated successfully.\n");
        } else {
            System.out.println("Comment not found.\n");
        }
    }

    public void deleteComment(int id) {
        if (comments.containsKey(id)) {
            comments.remove(id);
            System.out.println("Comment deleted successfully.\n");
        } else {
            System.out.println("Comment not found.\n");
        }
    }

    public void showReplies(int id) {
        Optional<Comment> opt = getComment(id);
        if (opt.isPresent()) {
            Comment comment = opt.get();
            System.out.println(comment);
            System.out.println("Upvotes: " + displayUpvotes(id) + " Downvotes: " + displayDownvotes(id));
            if (comment.getReplies().isEmpty()) {
                System.out.println("No replies yet.\n");
            } else {
                System.out.println("Replies:");
                for (Comment reply : comment.getReplies()) {
                    System.out.println(reply);
                }
            }
        } else {
            System.out.println("Comment not found.\n");
        }
    }

    public void addVote(int id, Vote vote) {
        Optional<Comment> opt = getComment(id);
        if (opt.isPresent()) {
            Comment comment = opt.get();
            comment.getVotes().add(vote);
            System.out.println("Vote added.\n");
        } else {
            System.out.println("Comment not found.\n");
        }
    }

    public int displayUpvotes(int id) {
        int counter = 0;
        List<Vote> votes;
        for (Integer key : comments.keySet()) {
            if (key == id) {
                votes = comments.get(key).getVotes();
                for (Vote vote : votes) {
                    if (vote.isUpvote() == true) {
                        counter++;
                    }
                }
                return counter;

            }
        }

        return 0;


    }

    public int displayDownvotes(int id) {
        List<Vote> votes;
        for (Integer key : comments.keySet()) {
            if (key == id) {
                votes = comments.get(key).getVotes();
                return votes.size() - displayUpvotes(id);

            }

        }
        return 0;

    }
}
