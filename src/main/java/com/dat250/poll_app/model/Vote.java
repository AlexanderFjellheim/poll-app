package com.dat250.poll_app.model;

import java.time.Instant;

public class Vote {
    private Instant publishedAt;

    public Vote() {}

    public Vote(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
