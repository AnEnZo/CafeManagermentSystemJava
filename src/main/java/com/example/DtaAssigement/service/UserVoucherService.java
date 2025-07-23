package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.UserVoucher;

import java.util.List;

public interface UserVoucherService {
    UserVoucher exchangePointsForVoucher(String username, String voucherCode);
    UserVoucher getById(Long id);
    List<UserVoucher> getVouchersForUser(String username);
}
