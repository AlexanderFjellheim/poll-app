package com.dat250.poll_app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "polls";
    public static final String APP_QUEUE = "poll-app.votes";
    public static final String BINDING_PATTERN = "poll.*.vote";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue appQueue() {
        return QueueBuilder.durable(APP_QUEUE).build();
        //return new Queue(APP_QUEUE, true);

    }

    @Bean
    public Binding binding(Queue appQueue, TopicExchange exchange) {
        return BindingBuilder.bind(appQueue).to(exchange).with(BINDING_PATTERN);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper om) {
        return new Jackson2JsonMessageConverter(om);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate t = new RabbitTemplate(connectionFactory);
        t.setExchange(EXCHANGE);
        t.setMessageConverter(messageConverter);
        return t;
    }
}
