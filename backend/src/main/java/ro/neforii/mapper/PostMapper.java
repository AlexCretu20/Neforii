package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostRequestDto;
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
                post.getAuthor(),
                post.getUser().getId(),
                post.getCreatedAt(),
                post.isAwarded(),
                post.getImagePath()
        );
    }

    public static PostRequestDto postToPostRequestDto(Post post){
        if (post == null) {
            return null;
        }

        return new PostRequestDto(
                post.getTitle(),
                post.getContent(),
                post.getImagePath(),
                post.getAuthor(),
                post.getId()
        );
    }

}
