package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.VoteOption;
import com.dat250.poll_app.service.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polls/{pollId}/options")
public class VoteOptionController {
    private final PollManager manager;

    public VoteOptionController(PollManager manager) { this.manager = manager; }

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