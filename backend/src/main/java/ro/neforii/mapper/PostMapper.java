package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.post.PostDto;
import ro.neforii.model.Post;

@Component
public class PostMapper {
    public static  PostDto postToPostDto(Post post){
        if (post == null){
            return null;
        }

        return new PostDto(
                post.getText(),
                post.getUser()
        );
    }

    public static Post postDtoToPost(PostDto postDto) {
        if (postDto == null) {
            return null;
        }
        return new Post(
                postDto.text(),
                postDto.user()

        );
    }
}
