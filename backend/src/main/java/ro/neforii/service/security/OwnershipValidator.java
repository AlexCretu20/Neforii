package ro.neforii.service.security;

import org.springframework.stereotype.Service;
import ro.neforii.exception.ForbiddenActionException;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;

import java.util.UUID;

@Service
public class OwnershipValidator {

    public void assertPostOwner(UUID userId, Post post) {
        if (!post.getUser().getId().equals(userId)) {
            throw new ForbiddenActionException("The current user is not the author of the post.");
        }
    }

    public void assertCommentOwner(UUID userId, Comment comment) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenActionException("The current user is not the author of the comment.");
        }
    }
}
