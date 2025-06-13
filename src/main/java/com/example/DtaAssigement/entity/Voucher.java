package com.example.DtaAssigement.entity;

import com.example.DtaAssigement.ennum.VoucherType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    //loại voucher có thể đổi

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @Schema(description = "Mã giảm giá")
    private String code;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Loại giảm giá", allowableValues = {"PERCENTAGE_DISCOUNT", "BUY_ONE_GET_ONE", "FIXED_DISCOUNT"})
    private VoucherType type;

    @Schema(description = "Giá trị giảm giá")
    @Column(precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Schema(description = "Số tiền tối thiểu để áp dụng voucher")
    @Column(precision = 15, scale = 2)
    private BigDecimal minOrderAmount;

    @Schema(description = "Trạng thái voucher")
    private boolean active;

    @Schema(description = "Điểm cần để đổi voucher")
    private Integer requiredPoints; // Số điểm cần để đổi

    @OneToMany(mappedBy = "voucher")
    @Schema(hidden = true)
    @JsonIgnore
    private List<Invoice> invoices = new ArrayList<>();
}
