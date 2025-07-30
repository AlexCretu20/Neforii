package ro.neforii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.Vote;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    Optional<Vote> findByPostAndUser(Post post, User user);

    Optional<Vote> findByCommentAndUser(Comment comment, User user);

    int countByPostAndIsUpvote(Post post, boolean isUpvote);

    int countByCommentAndIsUpvote(Comment comment, boolean isUpvote);


    void deleteByUserAndPost(User user, Post post);
}
