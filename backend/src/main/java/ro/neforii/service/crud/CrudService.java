package ro.neforii.service.crud;

public interface CrudService<E, ID, UpdateDto> extends
        ReadService<E, ID>,
        CreateService<E>,
        UpdateService<ID, UpdateDto, E>,
        DeleteService<ID> {
}