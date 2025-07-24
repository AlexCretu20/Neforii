package ro.neforii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.neforii.model.Post;

//JpaRepository si nu CrudRepository pentru ca are mai multe metode utile precum paginare si sortare(pt viitor)
public interface PostRepository extends JpaRepository<Post, Integer> {
}
