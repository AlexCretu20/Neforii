package ro.neforii.dto.common;

import ro.neforii.dto.post.PostResponseDto;

import java.util.List;

public record PageResponseCursor<T>(List<T> items, String nextCursor) {}
