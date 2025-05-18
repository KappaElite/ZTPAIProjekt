package com.example.ztpai.RabbitMQ;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class MessagesSender {

    private final RabbitTemplate rabbitTemplate;

    private final Queue queue;

    public MessagesSender(RabbitTemplate rabbitTemplate, Queue queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    public void send(){
        String message = "Hello RabbitMQ";
        rabbitTemplate.convertAndSend(queue.getName(), message);
        System.out.println("Sent message: " + message);
    }
}
