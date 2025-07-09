package repository;

import model.Post;

import java.util.List;
import java.util.Optional;

public class PostRepository implements ICrudRepository<Post>{
    @Override
    public void save(Post entity) {

    }

    @Override
    public Optional<Post> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Post> findAll() {
        return List.of();
    }




    @Override
    public void deleteById(int id) {

    }
}
