package ro.neforii.service.crud;

public interface CrudService<T, ID> extends
        ReadService<T, ID>,
        CreateService<T>,
        UpdateService<T, ID>,
        DeleteService<ID> {
}