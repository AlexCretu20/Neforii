package ro.neforii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.neforii.model.Comment;

//JpaRepository si nu CrudRepository pentru ca are mai multe metode utile precum paginare si sortare(pt viitor)
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
