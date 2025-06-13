package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.ChatMessageDTO;
import com.example.DtaAssigement.entity.Message;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.mapper.ChatMessageMapper;
import com.example.DtaAssigement.repository.MessageRepository;
import com.example.DtaAssigement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class ChatController {


    private SimpMessagingTemplate messagingTemplate;

    private UserRepository userRepository;
    private MessageRepository messageRepository;

    @MessageMapping("/chat") // client gửi tới /app/chat
    public void processMessage(ChatMessageDTO chatMessageDTO, Principal principal) {
        String senderUsername = principal.getName();

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User receiver = userRepository.findByUsername(chatMessageDTO.getReceiverUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Message message = Message.builder()
                .content(chatMessageDTO.getContent())
                .timestamp(LocalDateTime.now())
                .sender(sender)
                .receiver(receiver)
                .senderDisplayName(sender.getDisplayName())
                .build();

        messageRepository.save(message);

        ChatMessageDTO response = ChatMessageMapper.toDTO(message);

        // Gửi riêng đến receiver
        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/messages",
                response
        );
        // Gửi lại cho sender
        messagingTemplate.convertAndSendToUser(
                senderUsername,
                "/queue/messages",
                response
        );

    }
}


