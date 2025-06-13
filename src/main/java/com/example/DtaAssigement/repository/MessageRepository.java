package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Message;
import com.example.DtaAssigement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
}
