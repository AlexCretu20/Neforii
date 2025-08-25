package ro.neforii.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.neforii.model.Post;

import java.time.LocalDateTime;
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

    // uses offset pagination behind
    Slice<Post> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);


    @Query("""
         SELECT p
         FROM Post p
         ORDER BY p.createdAt DESC, p.id DESC
         """)
    List<Post> firstPage(Pageable pageable);

    // Next page (strictly older than the cursor (createdAt,id))
    @Query("""
         SELECT p
         FROM Post p
         WHERE (p.createdAt < :createdAt)
            OR (p.createdAt = :createdAt AND p.id < :id)
         ORDER BY p.createdAt DESC, p.id DESC
         """)
    List<Post> nextPage(
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") UUID id,
            Pageable pageable
    );
}
