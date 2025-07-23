package ro.neforii.dto.post;

import ro.neforii.model.User;

public record PostDto(
        String text,
        User user
) {
}

