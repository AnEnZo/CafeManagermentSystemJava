package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.OtpCode;
import com.example.DtaAssigement.repository.OtpCodeRepository;
import com.example.DtaAssigement.service.SmsService;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.SmsSubmissionResponseMessage;
import com.vonage.client.sms.messages.TextMessage;
import com.vonage.client.sms.MessageStatus;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SmsServiceImpl implements SmsService {

    private final OtpCodeRepository otpRepo;

    @Value("${vonage.api-key}")
    private String apiKey;

    @Value("${vonage.api-secret}")
    private String apiSecret;

    private VonageClient vonageClient;

    @PostConstruct
    public void init() {
        this.vonageClient = VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();
    }

    @Override
    public void sendOtp(String phoneNumber) {
        // tạo mã OTP 6 chữ số
        String code = String.format("%06d", new Random().nextInt(999999));

        otpRepo.deleteByPhoneNumber(phoneNumber);
        // save vào DB
        OtpCode otp = OtpCode.builder()
                .phoneNumber(phoneNumber)
                .code(code)
                .build();
        otpRepo.save(otp);

        // gửi SMS
        TextMessage message = new TextMessage(
                "Vonage APIs", // sender name (sẽ bị override ở Việt Nam)
                phoneNumber,
                "Ma OTP cua ban la: " + code + " (HSD 5 phut)"
        );

        try {
            SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
            SmsSubmissionResponseMessage responseMessage = response.getMessages().get(0);

            if (responseMessage.getStatus() == MessageStatus.OK) {
                log.info("Vonage SMS sent successfully.");
            } else {
                log.error("Vonage SMS failed: {}", responseMessage.getErrorText());
            }
        } catch (Exception e) {
            log.error("Lỗi gửi SMS qua Vonage", e);
        }
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String code) {
        return otpRepo.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .filter(otp -> !otp.isExpired())
                .map(otp -> otp.getCode().equals(code))
                .orElse(false);
    }
}
