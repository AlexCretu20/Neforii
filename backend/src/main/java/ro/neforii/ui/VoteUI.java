package ro.neforii.ui;

import ro.neforii.model.User;
import ro.neforii.service.VoteService;

import java.util.Scanner;

public class VoteUI {
    private final Scanner scanner;
    private final VoteService voteService;

    public VoteUI(Scanner scanner, VoteService voteService) {
        this.scanner = scanner;
        this.voteService = voteService;
    }

    public void createVoteUiPost(User currentUser, int postId) {
        System.out.println("1. Upvote");
        System.out.println("2. Downvote");
        String voteChoice = scanner.nextLine();
        if (voteChoice.equals("1") || voteChoice.equals("2")) {
            boolean isUpvote = voteChoice.equals("1");
            String result = voteService.createVote(currentUser.getId(), postId, null, isUpvote);
            System.out.println(result);
        }
    }

    public void createVoteUiComment(User currentUser, int commentId) {
        System.out.println("1. Upvote");
        System.out.println("2. Downvote");
        String voteChoice = scanner.nextLine();
        if (voteChoice.equals("1") || voteChoice.equals("2")) {
            boolean isUpvote = voteChoice.equals("1");
            String result = voteService.createVote(currentUser.getId(), null, commentId, isUpvote);
            System.out.println(result);
        }
    }
}
