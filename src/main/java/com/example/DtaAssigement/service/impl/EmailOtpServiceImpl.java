package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.EmailOtp;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.repository.EmailOtpRepository;
import com.example.DtaAssigement.service.EmailOtpService;
import com.example.DtaAssigement.service.UserService;
import jakarta.transaction.*;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
@AllArgsConstructor
public class EmailOtpServiceImpl implements EmailOtpService {
    private static final long EXPIRATION_MINUTES = 10;

     private EmailOtpRepository otpRepo;
     private UserService userService;
     private JavaMailSender mailSender;

     @Override
    public void createAndSendOtp(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Xóa OTP cũ
        otpRepo.deleteByUser(user);

        // Tạo OTP ngẫu nhiên 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setUser(user);
        emailOtp.setOtp(otp);
        emailOtp.setExpiryDate(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        otpRepo.save(emailOtp);

        // Gửi mail
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("OTP đặt lại mật khẩu");
        msg.setText("Mã OTP của bạn là: " + otp + " (hết hạn trong 10 phút)");
        mailSender.send(msg);
    }

    @Override
    public User validateOtpAndGetUser(String username, String otp) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        EmailOtp emailOtp = otpRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ"));

        if (emailOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP đã hết hạn");
        }
        if (!emailOtp.getOtp().equals(otp)) {
            throw new IllegalArgumentException("OTP không đúng");
        }
        // xóa OTP sau khi dùng
        otpRepo.delete(emailOtp);
        return user;
    }
}
