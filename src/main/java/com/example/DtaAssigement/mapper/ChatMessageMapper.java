package com.example.DtaAssigement.mapper;

import com.example.DtaAssigement.dto.ChatMessageDTO;
import com.example.DtaAssigement.entity.Message;
import com.example.DtaAssigement.entity.User;

public class ChatMessageMapper {

    // Từ Entity sang DTO
    public static ChatMessageDTO toDTO(Message message) {
        if (message == null) return null;

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderDisplayName(message.getSenderDisplayName());
        dto.setSenderUsername(message.getSender().getUsername());
        dto.setReceiverUsername(message.getReceiver().getUsername());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }

    // Từ DTO sang Entity
    public static Message toEntity(ChatMessageDTO dto, User sender, User receiver) {
        if (dto == null) return null;

        return Message.builder()
                .content(dto.getContent())
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : java.time.LocalDateTime.now())
                .senderDisplayName(dto.getSenderDisplayName())
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}

