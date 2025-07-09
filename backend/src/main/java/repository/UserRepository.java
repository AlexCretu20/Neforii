package repository;

import model.User;

import java.util.List;
import java.util.Optional;

public class UserRepository implements ICrudRepository<User> {

    @Override
    public void save(User entity) {

    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void update(User entity) {

    }

    @Override
    public void deleteById(int id) {

    }
}
