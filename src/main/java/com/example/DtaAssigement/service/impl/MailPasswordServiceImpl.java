package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.TokenResetPassword;
import com.example.DtaAssigement.repository.TokenResetPasswordRepository;
import com.example.DtaAssigement.service.MailPasswordService;
import com.example.DtaAssigement.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.*;
import com.example.DtaAssigement.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class MailPasswordServiceImpl implements MailPasswordService {
    private static final long EXPIRATION_HOURS = 24;

    @Autowired
    private TokenResetPasswordRepository tokenRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void createPasswordResetToken(String email, String appUrl) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));


        tokenRepo.deleteTokensByUser(user);

        String token = UUID.randomUUID().toString();
        TokenResetPassword prt = new TokenResetPassword();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));
        tokenRepo.save(prt);

        // Gửi mail
        String resetUrl = appUrl + "/api/auth/reset-password-mail?token=" + token;
        String subject = "Yêu cầu đặt lại mật khẩu";
        String body = "Chào " + user.getUsername() + ",\n\n"
                + "Bạn (hoặc ai đó) đã yêu cầu đặt lại mật khẩu.\n"
                + "Vui lòng truy cập liên kết sau để đặt lại mật khẩu (còn hiệu lực 24h):\n"
                + resetUrl + "\n\n"
                + "Nếu bạn không phải là người yêu cầu, hãy bỏ qua email này.";
        sendEmail(user.getEmail(), subject, body);
    }


    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    @Override
    public User validatePasswordResetToken(String token) {
        TokenResetPassword prt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));
        if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token đã hết hạn");
        }
        return prt.getUser();
    }
}
