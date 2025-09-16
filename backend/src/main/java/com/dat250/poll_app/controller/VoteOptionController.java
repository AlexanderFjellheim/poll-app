package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.Vote;
import com.dat250.poll_app.model.VoteOption;
import com.dat250.poll_app.service.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/polls/{pollId}/options")
public class VoteOptionController {
    private final PollManager manager;

    public VoteOptionController(PollManager manager) { this.manager = manager; }

    @GetMapping("/{optionId}/votes")
    public List<Vote> listVotesForOption(@PathVariable Long pollId, @PathVariable Long optionId) {
        return manager.listVotesForOption(pollId, optionId); // method we outlined earlier
    }

    @PostMapping public VoteOption add(@PathVariable Long pollId, @RequestBody VoteOption vo) {
        return manager.addOption(pollId, vo);
    }

    @PutMapping("/{optionId}") public VoteOption update(@PathVariable Long pollId, @PathVariable Long optionId, @RequestBody VoteOption vo) {
        return manager.updateOption(pollId, optionId, vo);
    }

    @DeleteMapping("/{optionId}") public ResponseEntity<Void> delete(@PathVariable Long pollId, @PathVariable Long optionId) {
        manager.deleteOption(pollId, optionId);
        return ResponseEntity.ok().build();
    }
}