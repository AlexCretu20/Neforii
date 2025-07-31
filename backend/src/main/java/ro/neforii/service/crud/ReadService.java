package ro.neforii.service.crud;

import java.util.List;

public interface ReadService<ResponseDto, ID> {
    ResponseDto findById(ID id);

    List<ResponseDto> findAll();
}