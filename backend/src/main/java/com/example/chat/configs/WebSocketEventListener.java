package com.example.chat.configs;

import com.example.chat.controllers.ChatController;
import com.example.chat.models.ChatMessage;
import com.example.chat.models.MessageType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    // ไม่ต้องมีตัวนับแยกที่นี่แล้ว เนื่องจากเราใช้ตัวนับ static ใน ChatController

    // เมธอดนี้จะถูกเรียกเมื่อ client subscribe ไปยัง destination ใด ๆ
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        // ตรวจสอบว่าหาก destination คือ /topic/userCount ให้ส่ง current count ทันที
        if ("/topic/userCount".equals(destination)) {
            int currentCount = ChatController.joinedUsers.get(); // ใช้ static counter จาก ChatController
            messagingTemplate.convertAndSend("/topic/userCount", currentCount);
            log.info("Subscription to {} detected. Sent current user count: {}", destination, currentCount);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionAttributes() != null) {
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            // ลด counter เฉพาะเมื่อ session มี username (หมายความว่าผู้ใช้ได้กด Join แล้ว)
            if (username != null) {
                // ส่งข้อความแจ้งว่าผู้ใช้ออกจากแชท
                ChatMessage chatMessage = ChatMessage.buildChatmessage(
                        username + " has left the chat.",
                        username,
                        MessageType.LEAVE
                );
                messagingTemplate.convertAndSend("/topic/messages", chatMessage);

                // ลดจำนวนผู้ใช้ที่เข้าร่วมแชทโดยเรียกใช้เมธอดจาก ChatController
                int count = ChatController.decrementUserCount();
                messagingTemplate.convertAndSend("/topic/userCount", count);
                log.info("User {} disconnected. Current joined users: {}", username, count);
            }
        }
    }
}
