package com.dat250.poll_app.controller;

import com.dat250.poll_app.event.VoteEvent;
import com.dat250.poll_app.messaging.VotePublisher;
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
    private final VotePublisher publisher;

    public VoteController(PollManager manager, VoteCountCacheService cache, VotePublisher publisher) {
        this.manager = manager;
        this.cache = cache;
        this.publisher = publisher;
    }

    @GetMapping
    public List<Vote> all() { return manager.listVotes(); }

    @PostMapping public Vote cast(@RequestParam(required = false) Long userId, @RequestParam Long optionId) {
        //return manager.castVote(userId, optionId);
        VoteOption option = manager.getOption(optionId);
        Long pollId = option.getPoll().getId();
        Integer newOrder = option.getPresentationOrder();

        Integer oldOrder = null;
        if (userId != null) {
            Optional<Vote> prev = manager.findUserVoteInPoll(userId, pollId);
            oldOrder = prev.map(v -> v.getOption().getPresentationOrder()).orElse(null);
        }

        Vote v = (userId == null) ? manager.castAnonymousVote(optionId) : manager.castVote(userId, optionId);

        if (oldOrder == null) {
            // User is voting for the first time in this poll (or anonymous vote)
            cache.increment(pollId, newOrder, +1);
        } else if (!oldOrder.equals(newOrder)) {
            // User changed their vote to a different option
            cache.increment(pollId, oldOrder, -1);
            cache.increment(pollId, newOrder, +1);
        }

        // Publish vote event
        VoteEvent voteEvent = new VoteEvent();
        voteEvent.pollId = option.getPoll().getId();
        voteEvent.optionId = optionId;
        voteEvent.userId = userId;
        voteEvent.source = "poll-app";

        publisher.publish(voteEvent);
        return v;
    }
}