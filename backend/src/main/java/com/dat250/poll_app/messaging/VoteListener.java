package com.dat250.poll_app.messaging;

import com.dat250.poll_app.config.RabbitMQConfig;
import com.dat250.poll_app.event.VoteEvent;
import com.dat250.poll_app.model.Vote;
import com.dat250.poll_app.model.VoteOption;
import com.dat250.poll_app.service.PollManager;
import com.dat250.poll_app.service.VoteCountCacheService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VoteListener {
    private final PollManager manager;
    private final VoteCountCacheService cache;

    public VoteListener(PollManager manager, VoteCountCacheService cache) {
        this.manager = manager;
        this.cache = cache;
    }

    @RabbitListener(queues = RabbitMQConfig.APP_QUEUE)
    public void onVote(VoteEvent voteEvent, @Header(name="source", required=false) String source) throws InterruptedException {
        System.out.printf("AMQP recv poll=%d option=%d user=%s src=%s%n",
                voteEvent.pollId, voteEvent.optionId, String.valueOf(voteEvent.userId), source);
        Thread.sleep(1000); // Simulate processing delay
        if ("poll-app".equals(source)) {
            return;
        }

        VoteOption newOption = manager.getOption(voteEvent.optionId);
        Long pollId = newOption.getPoll().getId();
        Integer newOrder = newOption.getPresentationOrder();

        Integer oldOrder = null;
        if (voteEvent.userId != null) {
            Optional<Vote> prev = manager.findUserVoteInPoll(voteEvent.userId, pollId);
            oldOrder = prev.map(v -> v.getOption().getPresentationOrder()).orElse(null);
        }

        Vote v = null;

        if (voteEvent.userId == null) {
            v = manager.castAnonymousVote(voteEvent.optionId);
        }
        else {
            v = manager.castVote(voteEvent.userId, voteEvent.optionId);
        }

        if (voteEvent.userId == null) {
            // Anonymous vote
            cache.increment(pollId, newOrder, +1);
        } else if (oldOrder == null) {
            // User is voting for the first time in this poll
            cache.increment(pollId, newOrder, +1);
        } else if (!oldOrder.equals(newOrder)) {
            // User changed their vote to a different option
            cache.increment(pollId, oldOrder, -1);
            cache.increment(pollId, newOrder, +1);
        }
    }
}
