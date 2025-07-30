package ro.neforii.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.neforii.dto.vote.VoteResponseDto;
import ro.neforii.mapper.VoteMapper;
import ro.neforii.model.Vote;
import ro.neforii.service.IVoteService;

import java.util.UUID;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private final IVoteService voteService;
    private final VoteMapper voteMapper;

    public VoteController(IVoteService voteService, VoteMapper voteMapper) {
        this.voteService = voteService;
        this.voteMapper = voteMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteResponseDto> getVoteById(@PathVariable UUID id) {
        Vote vote = voteService.getVoteById(id); // daca nu e null, da throw VoteNotFoundException si se stie controllerul sa se ocupe
        return ResponseEntity.ok(
                voteMapper.voteToVoteResponseDto(vote)
        );
    }
}
