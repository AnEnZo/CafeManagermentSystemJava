package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.dto.ResetPasswordRequest;
import com.example.DtaAssigement.dto.UserDTO;
import com.example.DtaAssigement.security.CustomUserDetails;
import com.example.DtaAssigement.service.EmailOtpService;
import com.example.DtaAssigement.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.*;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailOtpService emailOtpService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO dto = userService.getUserById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Email is already in use!");
        }
        UserDTO created = userService.createUser(userDTO);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createStaff(@Valid @RequestBody UserDTO userDTO) {
        if (userService.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Email is already in use!");
        }
        UserDTO created = userService.createStaff(userDTO);
        return ResponseEntity.status(201).body(created);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> searchUser(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUser(keyword));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        // Lấy username từ CustomUserDetails
        String username = currentUser.getUsername();
        UserDTO dto = userService.getUserByUsername(username);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> updateCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody Map<String, Object> updates
    ) {
        try {
            String username = currentUser.getUsername();
            UserDTO updated = userService.updateCurrentUser(username, updates);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('USER','ADMIN','STAFF')")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        try {
            // 1. Validate OTP và lấy User
            var user = emailOtpService.validateOtpAndGetUser(req.getUsername(), req.getOtp());

            // 2. Cập nhật password mới
            userService.updatePassword(user, req.getNewPassword());

            return ResponseEntity.ok("Password đã được cập nhật thành công");
        } catch (IllegalArgumentException | org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


}
