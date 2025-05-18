package com.example.ztpai.RabbitMQ;


import com.example.ztpai.DTO.MessageDTO;
import com.example.ztpai.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class DispatcherService {
    private final MessageService messageService;
    public DispatcherService(MessageService messageService) {
        this.messageService = messageService;
    }
    public void handle(MessageDTO messageDTO) {
        messageService.AddMesage(
                messageDTO.getContent(),
                messageDTO.getSender().getId(),
                messageDTO.getReceiver().getId()
        );
    }
}
