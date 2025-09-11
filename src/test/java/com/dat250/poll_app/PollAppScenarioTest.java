package com.dat250.poll_app;

import com.dat250.poll_app.model.Poll;
import com.dat250.poll_app.model.User;
import com.dat250.poll_app.model.Vote;
import com.dat250.poll_app.model.VoteOption;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class PollAppScenarioTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    // Replace maps with proper model objects
    static User user1;
    static User user2;
    static Poll poll;

    String url(String p) {
        return "http://localhost:" + port + p;
    }

    private <T> ResponseEntity<T> postJson(String path, Object body, Class<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return rest.exchange(url(path), HttpMethod.POST, new HttpEntity<>(body, headers), type);
    }

    // 1) Create a new user
    @Test @Order(1)
    void createUser1() {
        User newUser = new User();
        newUser.setUsername("Alice");
        newUser.setEmail("alice@mail.com");

        ResponseEntity<User> response = postJson("/users", newUser, User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        user1 = response.getBody();
        assertThat(user1).isNotNull();
        assertThat(user1.getId()).isNotNull();
        assertThat(user1.getUsername()).isEqualTo("Alice");
        assertThat(user1.getEmail()).isEqualTo("alice@mail.com");
    }

    // 2) List all users (-> shows the newly created user)
    @Test @Order(2)
    void listUsersAfter1() {
        ResponseEntity<User[]> response = rest.getForEntity(url("/users"), User[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        User[] users = response.getBody();
        assertThat(users).isNotNull();
        assertThat(users.length).isEqualTo(1);
        assertThat(users[0].getId()).isEqualTo(user1.getId());
        assertThat(users[0].getUsername()).isEqualTo(user1.getUsername());
    }

    // 3) Create another user
    @Test @Order(3)
    void createUser2() {
        User newUser = new User();
        newUser.setUsername("Bob");
        newUser.setEmail("bob@mail.com");

        ResponseEntity<User> response = postJson("/users", newUser, User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        user2 = response.getBody();
        assertThat(user2).isNotNull();
        assertThat(user2.getId()).isNotNull();
        assertThat(user2.getUsername()).isEqualTo("Bob");
        assertThat(user2.getEmail()).isEqualTo("bob@mail.com");
    }

    // 4) List all users again (-> shows two users)
    @Test @Order(4)
    void listUsersAfter2() {
        ResponseEntity<User[]> response = rest.getForEntity(url("/users"), User[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        User[] users = response.getBody();
        assertThat(users).isNotNull();
        assertThat(users.length).isEqualTo(2);
        assertThat(users[0].getId()).isIn(user1.getId(), user2.getId());
        assertThat(users[1].getId()).isIn(user1.getId(), user2.getId());
    }

    // 5) User 1 creates a new poll
    @Test @Order(5)
    void user1CreatesPoll() {
        Poll newPoll = new Poll();
        newPoll.setQuestion("Favorite color?");

        // Create poll options
        VoteOption option1 = new VoteOption();
        option1.setCaption("Red");
        option1.setPresentationOrder(1);

        VoteOption option2 = new VoteOption();
        option2.setCaption("Blue");
        option2.setPresentationOrder(2);

        newPoll.getOptions().add(option1);
        newPoll.getOptions().add(option2);

        // Set current time for published timestamp
        newPoll.setPublishedAt(Instant.now());

        ResponseEntity<Poll> response = postJson(
                "/polls?userId=" + user1.getId(),
                newPoll,
                Poll.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        poll = response.getBody();

        assertThat(poll).isNotNull();
        assertThat(poll.getId()).isNotNull();
        assertThat(poll.getQuestion()).isEqualTo("Favorite color?");
        assertThat(poll.getOptions().size()).isEqualTo(2);
        assertThat(poll.getCreator().getId()).isEqualTo(user1.getId());
    }

    // 6) List polls (-> shows the new poll)
    @Test @Order(6)
    void listPolls() {
        ResponseEntity<Poll[]> response = rest.getForEntity(url("/polls"), Poll[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Poll[] polls = response.getBody();
        assertThat(polls).isNotNull();
        assertThat(polls.length).isEqualTo(1);
        assertThat(polls[0].getId()).isEqualTo(poll.getId());
        assertThat(polls[0].getQuestion()).isEqualTo(poll.getQuestion());
    }

    // 7) User 2 votes on the poll
    @Test @Order(7)
    void user2Votes() {
        // Get the first option of the poll
        VoteOption firstOption = poll.getOptions().getFirst();

        ResponseEntity<Vote> response = postJson(
                "/votes?userId=" + user2.getId() + "&optionId=" + firstOption.getId(),
                null,
                Vote.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Vote vote = response.getBody();
        assertThat(vote).isNotNull();
        assertThat(vote.getId()).isNotNull();
        assertThat(vote.getUser().getId()).isEqualTo(user2.getId());
        assertThat(vote.getOption().getId()).isEqualTo(firstOption.getId());
        assertThat(vote.getPublishedAt()).isNotNull();
        assertThat(vote.getPublishedAt().isBefore(Instant.now())).isTrue();
    }

    // 8) User 2 changes his vote
    @Test @Order(8)
    void user2ChangesVote() {
        // Get the second option of the poll
        VoteOption secondOption = poll.getOptions().get(1);

        ResponseEntity<Vote> response = postJson(
                "/votes?userId=" + user2.getId() + "&optionId=" + secondOption.getId(),
                null,
                Vote.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Vote vote = response.getBody();
        assertThat(vote).isNotNull();
        assertThat(vote.getId()).isNotNull();
        assertThat(vote.getUser().getId()).isEqualTo(user2.getId());
        assertThat(vote.getOption().getId()).isEqualTo(secondOption.getId());
    }

    // 9) List votes (-> shows the most recent vote for User 2)
    @Test @Order(9)
    void listVotesShowsLatestOnly() {
        ResponseEntity<Vote[]> response = rest.getForEntity(url("/votes"), Vote[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Vote[] votes = response.getBody();
        assertThat(votes).isNotNull();
        assertThat(votes.length).isEqualTo(1);

        // The vote should be for the second option (the changed vote)
        VoteOption secondOption = poll.getOptions().get(1);
        assertThat(votes[0].getUser().getId()).isEqualTo(user2.getId());
        assertThat(votes[0].getOption().getId()).isEqualTo(secondOption.getId());
    }

    // 10) Delete the one poll
    @Test @Order(10)
    void deletePoll() {
        ResponseEntity<Void> response = rest.exchange(
                url("/polls/" + poll.getId()),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // 11) List votes (-> empty)
    @Test @Order(11)
    void votesAreEmptyAfterPollDeletion() {
        ResponseEntity<Vote[]> response = rest.getForEntity(url("/votes"), Vote[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Vote[] votes = response.getBody();
        assertThat(votes).isNotNull();
        assertThat(votes.length).isEqualTo(0);
    }
}