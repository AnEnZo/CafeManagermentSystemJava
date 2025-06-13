package com.example.DtaAssigement.entity;


import com.example.DtaAssigement.ennum.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "revenues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Revenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @OneToMany
    @JoinTable(
            name="revenue_invoices",
            joinColumns=@JoinColumn(name="revenue_id"),
            inverseJoinColumns=@JoinColumn(name="invoice_id")
    )
    private List<Invoice> invoices;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    @NotNull
    private Branch branch;

}
