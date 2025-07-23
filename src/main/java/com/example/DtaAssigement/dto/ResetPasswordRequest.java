package com.example.DtaAssigement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String username;
    @NotBlank private String otp;
    @NotBlank private String newPassword;
}