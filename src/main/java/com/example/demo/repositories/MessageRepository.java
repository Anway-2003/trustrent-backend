package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.Message;

public interface MessageRepository extends JpaRepository<Message, String> {

    // 1. Don users madhla purna chat history kadhnyasathi (Sender -> Receiver OR Receiver -> Sender)
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) OR (m.sender.id = :user2 AND m.receiver.id = :user1) ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(@Param("user1") String user1, @Param("user2") String user2);

    // 2. Eka user che sagle messages kadhnyasathi (Inbox dakhvnyasathi)
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.timestamp DESC")
    List<Message> findAllUserMessages(@Param("userId") String userId);
}