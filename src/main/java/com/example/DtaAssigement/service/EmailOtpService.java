package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.User;

public interface EmailOtpService {

    void createAndSendOtp(String username);
    User validateOtpAndGetUser(String username, String otp);
}
