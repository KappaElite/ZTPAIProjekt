package com.example.ztpai.RabbitMQ;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Configuration
public class QueueConfig {

    @Bean
    public Queue incomingQueue() {
        return new Queue("incoming");
    }
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue outgoingQueue() {
        return new Queue("outgoing");
    }

    @Bean
    public Filter messagesReceiver(RabbitTemplate rabbitTemplate, @Qualifier("outgoingQueue") Queue queue, FilterService filterService) {
        return new Filter(rabbitTemplate, queue,filterService);
    }

    @Bean
    public MessagesSender messagesSender(RabbitTemplate rabbitTemplate, @Qualifier("incomingQueue") Queue queue) {
        return new MessagesSender(rabbitTemplate, queue);
    }

    @Bean
    public Dispatcher dispatcher(DispatcherService dispatcherService, SimpMessagingTemplate messagingTemplate) {
        return new Dispatcher(dispatcherService, messagingTemplate);
    }


}
