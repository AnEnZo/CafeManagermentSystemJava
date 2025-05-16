package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.User;

public interface MailPasswordService {
    void createPasswordResetToken(String email, String appUrl);
    User validatePasswordResetToken(String token);
}
