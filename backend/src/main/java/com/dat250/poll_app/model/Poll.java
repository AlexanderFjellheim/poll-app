package com.dat250.poll_app.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Poll.class)
public class Poll {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    //@JsonBackReference("user-polls")
    @ManyToOne
    private User createdBy;

    //@JsonManagedReference("poll-options")
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options = new ArrayList<>();


    // constructors
    public Poll() {}

    /**
     * Creates a new Poll object with given question and creator.
     * The id of a new poll object gets determined by the database.
     * The publishedAt timestamp is set to the current time.
     * The validUntil timestamp is set to 24 hours from the current time.
     */
    public Poll(String question, User createdBy) {
        this.question = question;
        this.createdBy = createdBy;
    }

    public VoteOption addVoteOption(String caption) {
        Integer presentationOrder = this.options.size();
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
    public User getCreator() { return createdBy; }
    public void setCreator(User creator) { this.createdBy = creator; }
    public List<VoteOption> getOptions() { return options; }
    public void setOptions(List<VoteOption> options) { this.options = options; }
}
