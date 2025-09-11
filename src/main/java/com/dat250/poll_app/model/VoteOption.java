package com.dat250.poll_app.model;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.ArrayList;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = VoteOption.class)
public class VoteOption {
    private Long id;
    private String caption;
    private int presentationOrder;

    //@JsonBackReference("poll-options")
    private Poll poll;

    //@JsonBackReference("option-votes")
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();

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
