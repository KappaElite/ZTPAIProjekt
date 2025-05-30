package com.example.ztpai.RabbitMQ;
import com.example.ztpai.DTO.GroupMessageDTO;
import com.example.ztpai.DTO.MessageDTO;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RabbitListener(queues = "incoming")
public class Filter {

    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;
    private final FilterService filterService;

    public Filter(RabbitTemplate rabbitTemplate, Queue queue, FilterService filterService) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
        this.filterService = filterService;
    }

    @RabbitHandler
    public void receive(MessageDTO messageDTO) {
        rabbitTemplate.convertAndSend("outgoing", filterService.filter(messageDTO));
    }

    @RabbitHandler
    public void receive(GroupMessageDTO groupMessageDTO) {
        rabbitTemplate.convertAndSend("outgoing", filterService.filter(groupMessageDTO));
    }
}
