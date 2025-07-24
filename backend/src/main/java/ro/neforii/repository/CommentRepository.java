package ro.neforii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.neforii.model.Comment;

import java.util.List;
import java.util.Optional;

//JpaRepository si nu CrudRepository pentru ca are mai multe metode utile precum paginare si sortare(pt viitor)
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findById(int id);
    List<Comment> findAll();
    void deleteById(int id);
    List<Comment> findByPostId(int postId);
    List<Comment> findByCommentId(int commentId);

}
