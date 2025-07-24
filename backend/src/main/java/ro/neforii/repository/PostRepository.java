package ro.neforii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.neforii.model.Post;

import java.util.List;
import java.util.Optional;

//JpaRepository si nu CrudRepository pentru ca are mai multe metode utile precum paginare si sortare(pt viitor)
public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findById(int id);
    List<Post> findAll();
    void deleteById(int id);
    List<Post> findAllByUserId(int id);
}
