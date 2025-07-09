package repository;

import java.util.List;
import java.util.Optional;

public interface ICrudRepository <T>{
    void save(T entity);
    Optional<T> findById(int id);
    List<T> findAll();
    void deleteById(int id);
}
