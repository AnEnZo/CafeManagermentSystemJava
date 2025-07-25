package com.example.DtaAssigement.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @Schema(description = "Category ID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50)
    private String displayName;

    @NotBlank(message = "Username không được để trống")
    @Size(min = 6, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    private String username;

    // Chỉ dùng để bind từ request (POST/PUT), sẽ không bao giờ show ra JSON response
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 50, message = "Password phải từ 6 đến 100 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số")
    private String password;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "SĐT không được để trống")
    @Pattern(regexp = "^[0-9]{11}$", message = "SĐT không hợp lệ, vui lòng nhập lại")
    private String phoneNumber;

    @Schema(description = "Reward points accumulated by the user", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer rewardPoints;

    @Schema(description = "Roles granted to the user", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<String> roles;

    @Schema(description = "Authentication provider", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String provider;

    @Schema(description = "ID from OAuth provider", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String providerId;
}
