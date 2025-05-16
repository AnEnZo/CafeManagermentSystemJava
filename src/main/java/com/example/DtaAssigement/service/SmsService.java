package com.example.DtaAssigement.service;

public interface SmsService {
    void sendOtp(String phoneNumber);
    boolean verifyOtp(String phoneNumber, String code);
}
