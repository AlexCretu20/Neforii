package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.vote.VoteResponseDto;
import ro.neforii.model.Vote;

@Component
public class VoteMapper {
    public VoteResponseDto voteToVoteResponseDto(Vote vote) {
        if (vote == null) {
            return null;
        }
        return new VoteResponseDto(
                vote.getId(),
                vote.isUpvote(),
                vote.getCreatedAt(),
                vote.getPost().getId(),
                vote.getComment().getId()
        );
    }

}
