package com.dat250.poll_app.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PollManager {
    private Map<String, Poll> polls = new HashMap<>();
    private Map<String, User> users = new HashMap<>();



}
