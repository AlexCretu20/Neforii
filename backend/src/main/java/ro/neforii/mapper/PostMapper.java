package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.model.Post;

@Component
public class PostMapper {
    public static PostResponseDto postToPostDto(Post post){
        if (post == null){
            return null;
        }

        return new PostResponseDto(
                post.getId(),
                post.getText(),
                post.getUser().getId()
        );
    }

    public static Post postDtoToPost(PostResponseDto postResponseDto) {
        if (postResponseDto == null) {
            return null;
        }
        return new Post(
                postResponseDto.text(),
                postResponseDto.userId()

        );
    }
}
