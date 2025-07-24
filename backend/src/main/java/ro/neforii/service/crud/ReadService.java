package ro.neforii.service.crud;

import java.util.List;
import java.util.Optional;

public interface ReadService<T, ID> {
    Optional<T> findById(ID id);

    List<T> findAll();
}
