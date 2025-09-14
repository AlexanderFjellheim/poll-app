package com.dat250.poll_app.model;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.ArrayList;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
public class User {
    private Long id;
    private String username;
    private String email;

    //@JsonManagedReference("user-polls")
    @JsonIgnore
    private List<Poll> polls = new ArrayList<>();

    //@JsonManagedReference("user-votes")
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Poll> getPolls() { return polls; }
    public void setPolls(List<Poll> polls) { this.polls = polls; }
    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }
}
