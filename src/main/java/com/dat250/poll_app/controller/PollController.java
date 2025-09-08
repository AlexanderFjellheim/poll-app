package com.dat250.poll_app.controller;

import com.dat250.poll_app.model.PollManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/polls")
public class PollController {
    private PollManager pollManager;

}
