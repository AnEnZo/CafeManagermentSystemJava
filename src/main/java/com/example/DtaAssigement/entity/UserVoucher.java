package com.example.DtaAssigement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "UserVoucher ID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String code; // Mã riêng biệt, tự động sinh

    private boolean used ;

    @ManyToOne
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // Dùng để hiển thị QR Code hoặc check mã
    @Column(columnDefinition = "TEXT")
    private String qrCode;

    private LocalDateTime expiryAt;
}

