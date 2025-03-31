package com.example.ztpai.controller;


import com.example.ztpai.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class MessageController {

    List<Message> listOfMessages = new ArrayList<>();

    //@PostMapping("/new/{conversation_id}/{sender_id}")
    //public ResponseEntity newMessage(@RequestBody Message message, @PathVariable String conversation_id, @PathVariable String sender_id) {
        //try{
            //if(message.getMessageContent() == null){
                //return ResponseEntity.status(400).body("Empty message");
            //}
            //message.setConversation_id(conversation_id);
            //message.setSender_id(sender_id);
            //listOfMessages.add(message);
            //return ResponseEntity.status(201).body(listOfMessages.get(listOfMessages.size()-1).MessageInformation());
        //}
        //catch (Exception e){
            //return ResponseEntity.status(400).body("Error while adding new message");
        //}
   // }

    //@GetMapping("/get/{conversation_id}/{sender_id}")
    //public ResponseEntity getMessage(@PathVariable String conversation_id, @PathVariable String sender_id) {

        //tutaj będzie odwołanie do klasy z service która będzie odpowiadała za zebranie informacji z bazy i ułożenie tego w jedną całość
        //Message message = new Message(sender_id,conversation_id,"Test message for endpoint testing");
       // return ResponseEntity.ok(message);
    //}
}
