package ro.neforii.service.crud;

public interface UpdateService<ID, UpdateDto, ResponseDto> {
    ResponseDto update(ID id, UpdateDto dto);
}
