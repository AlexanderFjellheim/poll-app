package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.Poll;
import com.dat250.poll_app.service.PollManager;

import com.dat250.poll_app.service.VoteCountCacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/polls")
public class PollController {
    private final PollManager manager;
    private final VoteCountCacheService cache;

    public PollController(PollManager manager, VoteCountCacheService cache) {
        this.manager = manager;
        this.cache = cache;
    }

    @GetMapping
    public List<Poll> all() { return manager.listPolls(); }

    @PostMapping
    public Poll create(@RequestParam Long userId, @RequestBody Poll poll) {
        return manager.createPoll(userId, poll);
    }

    @PutMapping("/{id}")
    public Poll update(@PathVariable Long id, @RequestBody Poll upd) {
        return manager.updatePoll(id, upd);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cache.invalidate(id);
        manager.deletePoll(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/counts")
    public LinkedHashMap<Integer, Long> counts(@PathVariable Long id) {
        LinkedHashMap<Integer, Long> cached = cache.get(id);
        if (cached != null) {
            return cached;
        }
        LinkedHashMap<Integer, Long> counts = manager.countVotesPerOption(id);
        cache.put(id, counts);
        return counts;
    }
}