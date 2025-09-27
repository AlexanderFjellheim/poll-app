package com.dat250.poll_app.model;

import com.fasterxml.jackson.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
public class User {

    private Long id;
    private String username;
    private String email;

    //@JsonManagedReference("user-polls")
    @JsonIgnore
    private Set<Poll> polls = new LinkedHashSet<>();

    //@JsonManagedReference("user-votes")
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();


    // constructors

    public User() {}
    /**
     * Creates a new User object with given username and email.
     * The id of a new user object gets determined by the database.
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.polls = new LinkedHashSet<>();
    }

    /**
     * Creates a new Poll object for this user
     * with the given poll question
     * and returns it.
     */
    public Poll createPoll(String question) {
        // TODO: implement
        Poll poll = new Poll(question, this);
        this.polls.add(poll);
        return poll;
    }

    /**
     * Creates a new Vote for a given VoteOption in a Poll
     * and returns the Vote as an object.
     */
    public Vote voteFor(VoteOption option) {
        // TODO: implement
        Vote vote = new Vote(this, option);
        this.votes.add(vote);
        option.getVotes().add(vote);
        return vote;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Poll> getPolls() { return polls; }
    public void setPolls(Set<Poll> polls) { this.polls = polls; }
    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }
}
