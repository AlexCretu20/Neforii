package ro.neforii.service.crud;

public interface CrudService<ResponseDto, ID, CreateDto, UpdateDto> extends
        ReadService<ResponseDto, ID>,
        CreateService<CreateDto, ResponseDto>,
        UpdateService<ID, UpdateDto, ResponseDto>,
        DeleteService<ID> {
}