package com.example.chat.controllers;

import com.example.chat.dtos.CreateChatMessageBody;
import com.example.chat.models.ChatMessage;
import com.example.chat.models.MessageType;
import com.example.chat.models.User;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
@AllArgsConstructor
public class ChatController {
    private final SimpMessageSendingOperations messagingTemplate;

    // Static counter สำหรับผู้ที่กด Join (เข้าร่วมแชทจริง)
    public static final AtomicInteger joinedUsers = new AtomicInteger(0);

    public static int incrementUserCount() {
        return joinedUsers.incrementAndGet();
    }

    public static int decrementUserCount() {
        return joinedUsers.decrementAndGet();
    }

    @MessageMapping("/chat/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(CreateChatMessageBody createChatMessageBody) {
        String username = createChatMessageBody.getSender();
        String message = createChatMessageBody.getMessage();
        MessageType messageType = createChatMessageBody.getType();
        return ChatMessage.buildChatmessage(message, username, messageType);
    }

    @MessageMapping("/chat/addUser")
    @SendToUser("/queue/connected")
    public User addUser(CreateChatMessageBody createChatMessageBody, SimpMessageHeaderAccessor headerAccessor) {
        String username = createChatMessageBody.getSender();
        String message = createChatMessageBody.getMessage();
        MessageType messageType = createChatMessageBody.getType();

        // ตั้งค่า username ลงใน session attributes
        headerAccessor.getSessionAttributes().put("username", username);

        // เพิ่มจำนวนผู้ใช้ที่เข้าร่วมแชท
        int currentCount = ChatController.incrementUserCount();

        // ส่งข้อความแจ้งว่าผู้ใช้เข้าร่วมแชทไปยัง /topic/messages
        messagingTemplate.convertAndSend("/topic/messages",
                ChatMessage.buildChatmessage(message, username, messageType));

        // ส่งจำนวน current users ไปยัง /topic/userCount
        messagingTemplate.convertAndSend("/topic/userCount", currentCount);

        return new User(username);
    }
}
