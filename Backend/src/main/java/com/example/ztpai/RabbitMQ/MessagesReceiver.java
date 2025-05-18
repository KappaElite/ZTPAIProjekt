package com.example.ztpai.RabbitMQ;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = "hello")
public class MessagesReceiver {

    @RabbitHandler
    public void receive(String message) {
        System.out.println("Received message: " + message);
    }
}
