package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.Voucher;

import java.util.List;
import java.util.Optional;

public interface VoucherService {
    List<Voucher> getAllVouchers();
    Voucher createVoucher(Voucher voucher);
    Voucher updateVoucher(Long id, Voucher voucher);
    void deleteVoucher(Long id);
    Optional<Voucher> getVoucherById(Long id);
}
