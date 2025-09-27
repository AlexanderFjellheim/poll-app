package com.dat250.poll_app.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;


import java.util.List;
import java.util.ArrayList;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = VoteOption.class)
public class VoteOption {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String caption;
    private Integer presentationOrder;

    //@JsonBackReference("poll-options")
    @ManyToOne
    private Poll poll;

    //@JsonBackReference("option-votes")
    @OneToMany(mappedBy = "votesOn", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();

    // constructors
    public VoteOption() {}

    /**
     * Creates a new VoteOption object with given caption and associated poll.
     * The id of a new vote option gets determined by the database.
     * The presentationOrder is set to 0 by default and should be set later.
     */

    public VoteOption(String caption, Integer presentationOrder, Poll poll) {
        this.caption = caption;
        this.poll = poll;
        this.presentationOrder = presentationOrder;
        this.votes = new ArrayList<>();
        //poll.getOptions().add(this);
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public int getPresentationOrder() { return presentationOrder; }
    public void setPresentationOrder(int presentationOrder) { this.presentationOrder = presentationOrder; }
    public Poll getPoll() { return poll; }
    public void setPoll(Poll poll) { this.poll = poll; }
    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }
}
