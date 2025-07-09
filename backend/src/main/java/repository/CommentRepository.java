package repository;

import model.Comment;

import java.util.List;
import java.util.Optional;

public class CommentRepository implements ICrudRepository<Comment> {

    @Override
    public void save(Comment entity) {

    }

    @Override
    public Optional<Comment> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        return List.of();
    }



    @Override
    public void deleteById(int id) {

    }
}
