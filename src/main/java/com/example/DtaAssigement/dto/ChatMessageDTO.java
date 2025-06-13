package com.example.DtaAssigement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private String senderDisplayName;
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private LocalDateTime timestamp;
}

