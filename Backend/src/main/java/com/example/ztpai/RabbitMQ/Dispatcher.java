package com.example.ztpai.RabbitMQ;

import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@RabbitListener(queues = "outgoing")
public class Dispatcher {

    private final DispatcherService dispatcherService;
    private final SimpMessagingTemplate messagingTemplate;

    public Dispatcher(DispatcherService dispatcherService, SimpMessagingTemplate messagingTemplate) {
        this.dispatcherService = dispatcherService;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitHandler
    public void receive(MessageDTO messageDTO) {
        dispatcherService.handle(messageDTO);
        String destination = "/queue/messages/" + messageDTO.getReceiver().getId();
        String destination2 = "/queue/messages/" + messageDTO.getSender().getId();
        messagingTemplate.convertAndSend(destination, messageDTO);
        messagingTemplate.convertAndSend(destination2, messageDTO);
    }

    @RabbitHandler
    public void receive(GroupMessageDTO groupMessageDTO) {
        dispatcherService.handle(groupMessageDTO);
        String destination = "/topic/groupchat";
        messagingTemplate.convertAndSend(destination, groupMessageDTO);
    }
}
