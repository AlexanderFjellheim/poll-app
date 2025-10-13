package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.Vote;
import com.dat250.poll_app.model.VoteOption;
import com.dat250.poll_app.service.PollManager;
import com.dat250.poll_app.service.VoteCountCacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/polls/{pollId}/options")
public class VoteOptionController {
    private final PollManager manager;
    private final VoteCountCacheService cache;

    public VoteOptionController(PollManager manager, VoteCountCacheService cache) {
        this.manager = manager;
        this.cache = cache;
    }


    @PostMapping public VoteOption add(@PathVariable Long pollId, @RequestBody VoteOption vo) {
        VoteOption created = manager.addOption(pollId, vo);
        cache.invalidate(pollId);
        return created;
    }

    @PutMapping("/{optionId}") public VoteOption update(@PathVariable Long pollId, @PathVariable Long optionId, @RequestBody VoteOption vo) {
        VoteOption updated = manager.updateOption(pollId, optionId, vo);
        cache.invalidate(pollId);
        return updated;
    }

    @DeleteMapping("/{optionId}") public ResponseEntity<Void> delete(@PathVariable Long pollId, @PathVariable Long optionId) {
        manager.deleteOption(pollId, optionId);
        cache.invalidate(pollId);
        return ResponseEntity.ok().build();
    }
}