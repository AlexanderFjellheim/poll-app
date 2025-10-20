package com.dat250.poll_app.messaging;

import com.dat250.poll_app.event.VoteEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class VotePublisher {
    private final RabbitTemplate rabbitTemplate;
    public VotePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(VoteEvent voteEvent) {
        String routingKey = "poll." + voteEvent.pollId + ".vote";
        //rabbitTemplate.convertAndSend(routingKey, voteEvent);
        rabbitTemplate.convertAndSend(routingKey, voteEvent, m -> { m.getMessageProperties().setHeader("source", "poll-app"); return m; });
    }
}
