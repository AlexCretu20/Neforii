package models.comment;

public class CommentVoteRequestDto {
    private String voteType;

    public CommentVoteRequestDto() {}

    public CommentVoteRequestDto(String voteType) {
        this.voteType = voteType;
    }

    public String getVoteType() {
        return voteType;
    }

    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }
}
