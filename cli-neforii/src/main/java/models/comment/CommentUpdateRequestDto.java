package models.comment;

public class CommentUpdateRequestDto {
    private String content;

    public CommentUpdateRequestDto() {
    }

    public CommentUpdateRequestDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
