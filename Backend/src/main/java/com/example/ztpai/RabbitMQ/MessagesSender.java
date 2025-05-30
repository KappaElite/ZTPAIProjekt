package com.example.ztpai.RabbitMQ;
import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
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

    public void send(MessageDTO messageDTO) {
        rabbitTemplate.convertAndSend(queue.getName(), messageDTO);
        System.out.println("Sent message: " + messageDTO.getContent());
    }

    public void send(GroupMessageDTO groupMessageDTO) {
        rabbitTemplate.convertAndSend(queue.getName(), groupMessageDTO);
        System.out.println("Sent group message: " + groupMessageDTO.getContent());
    }
}
