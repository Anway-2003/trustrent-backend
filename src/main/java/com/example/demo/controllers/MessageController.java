package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.repositories.MessageRepository;
import com.example.demo.repositories.UserRepository;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000") // Frontend sathi allow
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Frontend kadun data ghenyasti ek chota class (DTO)
    public static class MessageRequest {
        public String senderId;
        public String receiverId;
        public String content;
    }

    // 1. Navin Message Pathavne (Send Message)
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest request) {
        User sender = userRepository.findById(request.senderId).orElse(null);
        User receiver = userRepository.findById(request.receiverId).orElse(null);

        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().body("Sender or Receiver not found in database!");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.content);

        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    // 2. Don users madhla purna chat history kadhne (WhatsApp style chat)
    @GetMapping("/history/{user1Id}/{user2Id}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable String user1Id, @PathVariable String user2Id) {
        List<Message> history = messageRepository.findChatHistory(user1Id, user2Id);
        return ResponseEntity.ok(history);
    }

    // 3. Eka user che sagle messages kadhne (Inbox page sathi)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Message>> getUserMessages(@PathVariable String userId) {
        List<Message> messages = messageRepository.findAllUserMessages(userId);
        return ResponseEntity.ok(messages);
    }
}