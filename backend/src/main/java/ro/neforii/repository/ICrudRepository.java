package ro.neforii.repository;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Optional;

public interface ICrudRepository<T> {
    void save(T entity);

    void update(T entity) throws OperationNotSupportedException; //get id of the entity to update, call findById, replace fields

    Optional<T> findById(int id);

    List<T> findAll();

    void deleteById(int id);
}
