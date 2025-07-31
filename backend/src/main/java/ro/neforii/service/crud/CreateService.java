package ro.neforii.service.crud;

public interface CreateService<CreateDto, ResponseDto> {
    ResponseDto create(CreateDto dto);
}