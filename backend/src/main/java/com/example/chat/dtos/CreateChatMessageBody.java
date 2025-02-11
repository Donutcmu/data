package com.example.chat.dtos;

import com.example.chat.models.MessageType;
import lombok.Getter;


@Getter
public class CreateChatMessageBody {
    private String message;
    private String sender;
    private MessageType type;
}
