package ro.neforii.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.neforii.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//JpaRepository si nu CrudRepository pentru ca are mai multe metode utile precum paginare si sortare(pt viitor)
@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Optional<Post> findById(UUID id);

    List<Post> findAll();

    void deleteById(UUID id);

    List<Post> findAllByUserId(UUID id);

    boolean existsByTitle(String title);
}
