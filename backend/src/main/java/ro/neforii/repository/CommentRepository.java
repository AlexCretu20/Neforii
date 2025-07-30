package ro.neforii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.neforii.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//JpaRepository si nu CrudRepository pentru ca are mai multe metode utile precum paginare si sortare(pt viitor)
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Optional<Comment> findById(UUID id);

    List<Comment> findAll();

    void deleteById(UUID id);

    List<Comment> findByPostId(UUID postId);

    List<Comment> findByParentCommentId(UUID parentCommentId);
}
