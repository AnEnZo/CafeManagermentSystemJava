package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.UserVoucher;

public interface UserVoucherService {
    UserVoucher exchangePointsForVoucher(String username, String voucherCode);
    UserVoucher getById(Long id);
}
