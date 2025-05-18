package com.example.ztpai.RabbitMQ;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class MessagesFilter {

    @Bean
    public Queue messagesQueue() {
        return new Queue("hello");
    }

    @Bean
    public MessagesReceiver messagesReceiver() {
        return new MessagesReceiver();
    }

    @Bean
    public MessagesSender messagesSender(RabbitTemplate rabbitTemplate, Queue queue) {
        return new MessagesSender(rabbitTemplate, queue);
    }

}
