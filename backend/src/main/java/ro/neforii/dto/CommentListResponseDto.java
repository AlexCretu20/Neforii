package ro.neforii.dto;

import ro.neforii.dto.comment.CommentResponseDto;

import java.util.List;

public record CommentListResponseDto(
        List<CommentResponseDto> data,
        int total,
        boolean success
) {
    public CommentListResponseDto(List<CommentResponseDto> data, int total) {
        this(data, total, true);
    }
}
