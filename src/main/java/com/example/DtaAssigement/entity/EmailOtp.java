package com.example.DtaAssigement.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "email_otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailOtp {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String otp;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // getters/setters...
}
