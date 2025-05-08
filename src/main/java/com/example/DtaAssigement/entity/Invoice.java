package com.example.DtaAssigement.entity;

import com.example.DtaAssigement.ennum.PaymentMethod;
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
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    private LocalDateTime paymentTime;

    private double totalAmount;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "cashier_id")
    private User cashier;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
