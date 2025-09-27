package com.dat250.poll_app.model;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Vote.class)
public class Vote {
    private Long id;
    private Instant publishedAt;

    //@JsonBackReference("user-votes")
    private User user;

    //@JsonManagedReference("option-votes")
    private VoteOption option;

    // constructors
    public Vote() {}

    /**
     * Creates a new Vote object for given user and vote option.
     * The id of a new vote object gets determined by the database.
     * The publishedAt timestamp is set to the current time.
     */
    public Vote(User user, VoteOption option) {
        this.user = user;
        this.option = option;
        this.publishedAt = Instant.now();
        user.getVotes().add(this);
        option.getVotes().add(this);
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public VoteOption getOption() { return option; }
    public void setOption(VoteOption option) { this.option = option; }
}
