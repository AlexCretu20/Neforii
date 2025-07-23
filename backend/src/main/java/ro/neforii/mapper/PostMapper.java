package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.model.Post;

@Component
public class PostMapper {
    public static PostRequestDto postToPostRequestDto(Post post) {
        if (post == null) {
            return null;
        }

        return new PostRequestDto(
                post.getText(),
                post.getUser().getId()
        );
    }

    public static Post postRequestDtoToPost(PostRequestDto postRequestDto) {
        if (postRequestDto == null) {
            return null;
        }
        return new Post(
                //am comentat ca nu facea build ca userId nu e in Post
                //to do
//                postRequestDto.text(),
//                postRequestDto.userId()
        );
    }

    public static PostResponseDto postToPostResponseDto(Post post) {
        if (post == null) {
            return null;
        }

        return new PostResponseDto(
                post.getId(),
                post.getText(),
                post.getUser().getId(),
                post.getCreatedAt(),
                post.isAwarded()
        );
    }

    public static Post postResponseToPost(PostResponseDto postResponseDto) {
        if (postResponseDto == null) {
            return null;
        }

        return new Post(
//                postResponseDto.text(),
//                postResponseDto.createdAt(),
//                postResponseDto.isAwarded();
                //to do
//                postResponseDto.userId()
        );
    }
}
