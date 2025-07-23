package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.ResetPasswordRequest;
import com.example.DtaAssigement.ennum.AuthProvider;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.payload.JwtResponse;
import com.example.DtaAssigement.payload.LoginRequest;
import com.example.DtaAssigement.payload.RegisterRequest;
import com.example.DtaAssigement.repository.UserRepository;
import com.example.DtaAssigement.security.JwtTokenUtil;
import com.example.DtaAssigement.service.EmailOtpService;
import com.example.DtaAssigement.service.SmsService;
import com.example.DtaAssigement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {


    private UserService userService;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private final SmsService smsService;
    private final UserRepository userRepo;
    private final EmailOtpService emailOtpService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);
        if (user == null || user.getProvider() != AuthProvider.LOCAL) {
            return ResponseEntity.status(403).body("Invalid username or password");
        }
        if (user.getProvider() != AuthProvider.LOCAL) {
            return ResponseEntity.status(403).body("account not exit! ");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Email is already in use!");
        }

        userService.registerNewUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully!");
    }

    @PostMapping("/forgot-password-email")
    public ResponseEntity<?> forgotPasswordEmail(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body("username is required");
        }
        try {
            emailOtpService.createAndSendOtp(username);
            return ResponseEntity.ok(Map.of(
                    "message", "OTP đã được gửi đến email"));
        } catch (UsernameNotFoundException ex) {
            // không tiết lộ user có tồn tại hay không
            return ResponseEntity.ok(Map.of(
                    "message", "OTP đã được gửi đến email"));
        } catch (MailException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi email");
        }
    }

    @PostMapping("/reset-password-email")
    public ResponseEntity<?> resetPasswordEmail(@RequestBody ResetPasswordRequest req) {
        try {
            User user = emailOtpService.validateOtpAndGetUser(req.getUsername(), req.getOtp());
            userService.updatePassword(user, req.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password-sms")
    public ResponseEntity<?> forgotPasswordBySms(@RequestParam("username") String username) {
        userService.findByUsername(username).ifPresent(user -> {
            smsService.sendOtp(user.getPhoneNumber());
        });
        // Luôn trả về thông báo OTP
        return ResponseEntity.ok("Đã gửi mã OTP đến số điện thoại.");
    }

    @PostMapping("/reset-password-sms")
    public ResponseEntity<?> resetPasswordBySms(@RequestParam("username") String username,
                                                @RequestParam("otp") String otp,
                                                @RequestParam("newPassword") String newPassword) {

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        if (!smsService.verifyOtp(user.getPhoneNumber(), otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ hoặc đã hết hạn.");
        }

        userService.updatePassword(user, newPassword);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }

}
