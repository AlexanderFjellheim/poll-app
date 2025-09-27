package com.dat250.poll_app.model;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Poll.class)
public class Poll {
    private Long id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    //@JsonBackReference("user-polls")
    private User creator;

    //@JsonManagedReference("poll-options")
    private List<VoteOption> options = new ArrayList<>();

    // constructors
    public Poll() {}

    /**
     * Creates a new Poll object with given question and creator.
     * The id of a new poll object gets determined by the database.
     * The publishedAt timestamp is set to the current time.
     * The validUntil timestamp is set to 24 hours from the current time.
     */
    public Poll(String question, User creator) {
        this.question = question;
        this.creator = creator;
        this.publishedAt = Instant.now();
        this.validUntil = publishedAt.plusSeconds(24 * 3600);
        this.options = new ArrayList<>();
        creator.getPolls().add(this);
    }

    public VoteOption addVoteOption(String caption) {
        // TODO: implement
        Integer presentationOrder = options.size();
        VoteOption option = new VoteOption(caption, presentationOrder, this);
        this.options.add(option);
        return option;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public Instant getValidUntil() { return validUntil; }
    public void setValidUntil(Instant validUntil) { this.validUntil = validUntil; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public List<VoteOption> getOptions() { return options; }
    public void setOptions(List<VoteOption> options) { this.options = options; }
}
