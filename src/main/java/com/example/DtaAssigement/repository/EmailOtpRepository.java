package com.example.DtaAssigement.repository;


import com.example.DtaAssigement.entity.EmailOtp;
import com.example.DtaAssigement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findByUser(User user);
    void deleteByUser(User user);
}

