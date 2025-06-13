package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.ennum.AuthProvider;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.payload.JwtResponse;
import com.example.DtaAssigement.payload.LoginRequest;
import com.example.DtaAssigement.payload.RegisterRequest;
import com.example.DtaAssigement.repository.UserRepository;
import com.example.DtaAssigement.security.JwtTokenUtil;
import com.example.DtaAssigement.service.MailPasswordService;
import com.example.DtaAssigement.service.SmsService;
import com.example.DtaAssigement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {


    private UserService userService;

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    private MailPasswordService mailPasswordService;

    private final SmsService smsService;

    private final UserRepository userRepo;

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



    @PostMapping("/forgot-password-mail")
    public ResponseEntity<?> forgotPassword(@RequestParam("username") String username,
                                            HttpServletRequest request) {

        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

            String email = user.getEmail();

            String appUrl = request.getRequestURL()
                    .toString().replace(request.getRequestURI(), request.getContextPath());
            mailPasswordService.createPasswordResetToken(email, appUrl);
            return ResponseEntity.ok("Đã gửi email hướng dẫn đặt lại mật khẩu");
        } catch (UsernameNotFoundException ex) {
            // Không tiết lộ email không tồn tại
            return ResponseEntity.ok("Đã gửi email hướng dẫn đặt lại mật khẩu");
        }
    }

    @GetMapping("/reset-password-mail")
    public ModelAndView showResetPasswordPage(@RequestParam("token") String token) {
        try {
            mailPasswordService.validatePasswordResetToken(token); // kiểm tra token
            ModelAndView mav = new ModelAndView("reset-password-form"); // trả về tên view (HTML)
            mav.addObject("token", token);
            return mav;
        } catch (IllegalArgumentException e) {
            return new ModelAndView("error").addObject("message", e.getMessage());
        }
    }


    @PostMapping("/reset-password-mail")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestParam("password") String password) {

        try {
            User user = mailPasswordService.validatePasswordResetToken(token);
            userService.updatePassword(user, password);
            return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
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
