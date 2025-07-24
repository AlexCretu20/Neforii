package ro.neforii.service.crud;

public interface UpdateService<T, ID> {
    T update(ID id, T entity);
}
