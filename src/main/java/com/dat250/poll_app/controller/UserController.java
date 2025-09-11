package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.User;
import com.dat250.poll_app.service.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {
    private final PollManager manager;

    public UserController(PollManager manager) { this.manager = manager; }

    @GetMapping
    public List<User> all() { return manager.listUsers(); }

    @PostMapping public User create(@RequestBody User u) { return manager.createUser(u); }

    @PutMapping("/{id}") public User update(@PathVariable Long id, @RequestBody User u) { return manager.updateUser(id, u); }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        manager.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}