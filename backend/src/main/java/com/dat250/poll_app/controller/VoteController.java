package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.Vote;
import com.dat250.poll_app.model.VoteOption;
import com.dat250.poll_app.service.PollManager;
import com.dat250.poll_app.service.VoteCountCacheService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/votes")
public class VoteController {
    private final PollManager manager;
    private final VoteCountCacheService cache;

    public VoteController(PollManager manager, VoteCountCacheService cache) {
        this.manager = manager;
        this.cache = cache;
    }

    @GetMapping
    public List<Vote> all() { return manager.listVotes(); }

    @PostMapping public Vote cast(@RequestParam Long userId, @RequestParam Long optionId) {
        //return manager.castVote(userId, optionId);
        VoteOption option = manager.getOption(optionId);
        Long pollId = option.getPoll().getId();
        Integer newOrder = option.getPresentationOrder();

        // Check cache for existing vote by user in this poll
        Optional<Vote> prev = manager.findUserVoteInPoll(userId, pollId);
        Integer oldOrder = prev.map(v -> v.getOption().getPresentationOrder()).orElse(null);

        // Perform the vote operation
        Vote v = manager.castVote(userId, optionId);

        if (oldOrder == null) {
            // User is voting for the first time in this poll
            cache.increment(pollId, newOrder, +1);
        } else if (!oldOrder.equals(newOrder)) {
            // User changed their vote to a different option
            cache.increment(pollId, oldOrder, -1);
            cache.increment(pollId, newOrder, +1);
        }
        return v;
    }
}