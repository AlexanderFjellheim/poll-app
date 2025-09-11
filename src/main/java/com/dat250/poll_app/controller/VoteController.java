package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.Vote;
import com.dat250.poll_app.service.PollManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/votes")
public class VoteController {
    private final PollManager manager;

    public VoteController(PollManager manager) { this.manager = manager; }

    @GetMapping
    public List<Vote> all() { return manager.listVotes(); }

    @PostMapping public Vote cast(@RequestParam Long userId, @RequestParam Long optionId) {
        return manager.castVote(userId, optionId);
    }
}