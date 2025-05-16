package com.example.DtaAssigement.ennum;

import io.swagger.v3.oas.annotations.media.Schema;

public enum VoucherType {
    @Schema(description = "Giảm theo phần trăm")
    PERCENTAGE_DISCOUNT,

    @Schema(description = "Mua 1 tặng 1")
    BUY_ONE_GET_ONE,

    @Schema(description = "Giảm số tiền cố định")
    FIXED_DISCOUNT
}
