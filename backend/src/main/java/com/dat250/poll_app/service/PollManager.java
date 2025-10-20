package com.dat250.poll_app.service;

import com.dat250.poll_app.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PollManager {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Poll> polls = new HashMap<>();
    private final Map<Long, VoteOption> options = new HashMap<>();
    private final Map<Long, Vote> votes = new HashMap<>();

    private final AtomicLong userSeq = new AtomicLong(1);
    private final AtomicLong pollSeq = new AtomicLong(1);
    private final AtomicLong optionSeq = new AtomicLong(1);
    private final AtomicLong voteSeq = new AtomicLong(1);

    // ---------- USERS ----------
    public User createUser(User u) {
        u.setId(userSeq.getAndIncrement());
        u.setPolls(new HashSet<>());
        u.setVotes(new ArrayList<>());
        users.put(u.getId(), u);
        return u;
    }

    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }



    public User updateUser(Long id, User upd) {
        User u = users.get(id);
        if (u == null) throw new NoSuchElementException("User not found");
        u.setUsername(upd.getUsername());
        u.setEmail(upd.getEmail());
        return u;
    }

    public void deleteUser(Long id) {
        User u = users.remove(id);
        if (u == null) return;

        // delete polls created by user
        for (Poll p : new ArrayList<>(u.getPolls())) {
            deletePoll(p.getId());
        }
        // delete votes cast by user
        for (Vote v : new ArrayList<>(u.getVotes())) {
            removeVote(v);
        }
    }

    // ---------- POLLS ----------
    public Poll createPoll(Long creatorId, Poll poll) {
        User creator = users.get(creatorId);
        if (creator == null) throw new NoSuchElementException("Creator not found");

        poll.setId(pollSeq.getAndIncrement());
        poll.setCreator(creator);
        if (poll.getPublishedAt() == null) poll.setPublishedAt(Instant.now());
        if (poll.getOptions() == null) poll.setOptions(new ArrayList<>());

        // attach options
        poll.getOptions().sort(Comparator.comparingInt(VoteOption::getPresentationOrder));
        for (VoteOption opt : poll.getOptions()) {
            opt.setId(optionSeq.getAndIncrement());
            opt.setPoll(poll);
            opt.setVotes(new ArrayList<>());
            options.put(opt.getId(), opt);
        }

        creator.getPolls().add(poll);
        polls.put(poll.getId(), poll);

        return poll;
    }

    public List<Poll> listPolls() { return new ArrayList<>(polls.values()); }


    public Poll updatePoll(Long id, Poll upd) {
        Poll p = polls.get(id);
        if (p == null) throw new NoSuchElementException("Poll not found");
        p.setQuestion(upd.getQuestion());
        p.setValidUntil(upd.getValidUntil());
        // publishedAt intentionally not changed
        return p;
    }

    public void deletePoll(Long id) {
        Poll p = polls.remove(id);
        if (p == null) return;

        if (p.getCreator() != null) p.getCreator().getPolls().remove(p);
        for (VoteOption opt : new ArrayList<>(p.getOptions())) {
            for (Vote v : new ArrayList<>(opt.getVotes())) removeVote(v);
            options.remove(opt.getId());
        }
    }

    // ---------- OPTIONS (scoped under a poll) ----------
    public VoteOption addOption(Long pollId, VoteOption vo) {
        Poll p = polls.get(pollId);
        if (p == null) throw new NoSuchElementException("Poll not found");
        vo.setId(optionSeq.getAndIncrement());
        vo.setPoll(p);
        vo.setVotes(new ArrayList<>());
        p.getOptions().add(vo);
        p.getOptions().sort(Comparator.comparingInt(VoteOption::getPresentationOrder));
        options.put(vo.getId(), vo);
        return vo;
    }

    public VoteOption updateOption(Long pollId, Long optionId, VoteOption upd) {
        Poll p = polls.get(pollId);
        if (p == null) throw new NoSuchElementException("Poll not found");
        VoteOption vo = options.get(optionId);
        if (vo == null || !Objects.equals(vo.getPoll().getId(), pollId))
            throw new NoSuchElementException("Option not found in poll");
        vo.setCaption(upd.getCaption());
        vo.setPresentationOrder(upd.getPresentationOrder());
        p.getOptions().sort(Comparator.comparingInt(VoteOption::getPresentationOrder));
        return vo;
    }

    public void deleteOption(Long pollId, Long optionId) {
        VoteOption vo = options.remove(optionId);
        if (vo == null) return;
        Poll p = vo.getPoll();
        if (p != null && Objects.equals(p.getId(), pollId)) {
            for (Vote v : new ArrayList<>(vo.getVotes())) removeVote(v);
            p.getOptions().remove(vo);
        }
    }

    public VoteOption getOption(Long optionId) {
        VoteOption vo = options.get(optionId);
        if (vo == null) throw new NoSuchElementException("Option not found in poll");
        return vo;
    }

    public Optional<Vote> findUserVoteInPoll(Long userId, Long pollId) {
        User u = users.get(userId);
        if (u == null || u.getVotes() == null) return Optional.empty();
        for (Vote v : u.getVotes()) {
            VoteOption opt = v.getOption();
            if (opt != null && opt.getPoll() != null && Objects.equals(opt.getPoll().getId(), pollId)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    // ---------- VOTES ----------
    public Vote castVote(Long userId, Long optionId) {
        User user = users.get(userId);
        if (user == null) throw new NoSuchElementException("User not found");
        VoteOption opt = options.get(optionId);
        if (opt == null) throw new NoSuchElementException("Option not found");

        Poll poll = opt.getPoll();
        Instant now = Instant.now();
        if (poll.getPublishedAt() != null && now.isBefore(poll.getPublishedAt()))
            throw new IllegalStateException("Voting not started");
        if (poll.getValidUntil() != null && now.isAfter(poll.getValidUntil()))
            throw new IllegalStateException("Voting closed");

        // change vote: remove existing vote of this user in this poll
        for (Vote v : new ArrayList<>(user.getVotes())) {
            if (v.getOption().getPoll().getId().equals(poll.getId())) {

                if (v.getOption().getId().equals(optionId)) {
                    return v;
                }

                removeVote(v);
                break;
            }
        }

        Vote v = new Vote();
        v.setId(voteSeq.getAndIncrement());
        v.setPublishedAt(now);
        v.setUser(user);
        v.setOption(opt);

        user.getVotes().add(v);
        opt.getVotes().add(v);
        votes.put(v.getId(), v);
        return v;
    }

    public List<Vote> listVotes() { return new ArrayList<>(votes.values()); }

    private void removeVote(Vote v) {
        System.out.println("Removing vote " + v.getId() + " by user " +
                (v.getUser() != null ? v.getUser().getId() : "null") +
                " on option " +
                (v.getOption() != null ? v.getOption().getId() : "null"));

        votes.remove(v.getId());
        if (v.getUser() != null) v.getUser().getVotes().remove(v);
        if (v.getOption() != null) v.getOption().getVotes().remove(v);
    }

    public LinkedHashMap<Integer, Long> countVotesPerOption(Long id) {
        Poll p = polls.get(id);
        if (p == null) throw new NoSuchElementException("Poll not found: " + id);

        var optionsSorted = new ArrayList<>(p.getOptions());
        optionsSorted.sort(Comparator.comparingInt(VoteOption::getPresentationOrder));

        var result = new LinkedHashMap<Integer, Long>();
        for (VoteOption opt : optionsSorted) {
            int order = opt.getPresentationOrder();
            long count = (opt.getVotes() == null) ? 0L : opt.getVotes().size();
            result.put(order, count);
        }
        return result;
    }

    public Vote castAnonymousVote(Long optionId) {
        VoteOption opt = options.get(optionId);
        if (opt == null) throw new NoSuchElementException("Option not found");

        Vote v = new Vote();
        Instant now = Instant.now();
        v.setId(voteSeq.getAndIncrement());
        v.setPublishedAt(now);
        v.setUser(null); // anonymous
        v.setOption(opt);

        votes.put(v.getId(), v);
        opt.getVotes().add(v);
        return v;
    }
}
