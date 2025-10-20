package com.dat250.poll_app.messaging;

import com.dat250.poll_app.event.VoteEvent;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

public class StandaloneRabbitMQVoteProducer {
    public static void main(String[] args) {
        var ccf = new CachingConnectionFactory("localhost");
        var rabbitTemplate = new RabbitTemplate(ccf);
        rabbitTemplate.setExchange("polls");
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        var voteEvent = new VoteEvent();
        long pollId = Long.parseLong(args[0]);
        long optionId = Long.parseLong(args[1]);
        voteEvent.pollId = pollId;
        voteEvent.optionId = optionId;
        voteEvent.userId = null;
        voteEvent.source = "client";
        rabbitTemplate.convertAndSend("poll."+pollId+".vote", voteEvent, m -> { m.getMessageProperties().setHeader("source","client"); return m; });
        ccf.destroy();
    }
}
