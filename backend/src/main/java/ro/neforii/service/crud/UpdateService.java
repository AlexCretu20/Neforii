package ro.neforii.service.crud;

public interface UpdateService<ID, DTO, E> {
    E update(ID id, DTO dto);
}
