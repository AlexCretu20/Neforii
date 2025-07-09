package repository;

import model.Vote;

import java.util.List;
import java.util.Optional;

public class VoteRepository implements ICrudRepository<Vote> {
    @Override
    public void save(Vote entity) {

    }

    @Override
    public Optional<Vote> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Vote> findAll() {
        return List.of();
    }


    @Override
    public void deleteById(int id) {

    }
}
