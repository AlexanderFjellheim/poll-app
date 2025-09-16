package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.Poll;
import com.dat250.poll_app.service.PollManager;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/polls")
public class PollController {
    private final PollManager manager;

    public PollController(PollManager manager) { this.manager = manager; }

    @GetMapping public List<Poll> all() { return manager.listPolls(); }

    @PostMapping public Poll create(@RequestParam Long userId, @RequestBody Poll poll) {
        return manager.createPoll(userId, poll);
    }

    @PutMapping("/{id}") public Poll update(@PathVariable Long id, @RequestBody Poll upd) {
        return manager.updatePoll(id, upd);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        manager.deletePoll(id);
        return ResponseEntity.ok().build();
    }
}