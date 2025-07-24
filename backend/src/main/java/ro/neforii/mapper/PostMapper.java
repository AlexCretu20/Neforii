package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.model.Post;

@Component
public class PostMapper {

    public PostResponseDto postToPostResponseDto(Post post) {
        if (post == null) {
            return null;
        }

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getId(),
                post.getCreatedAt(),
                post.isAwarded(),
                post.getImagePath()
        );
    }

}
