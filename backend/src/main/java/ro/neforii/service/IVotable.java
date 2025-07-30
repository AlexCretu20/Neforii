package ro.neforii.service;

import java.util.UUID;

public interface IVotable {
    int displayUpvotes(UUID id);

    int displayDownvotes(UUID id);
}
