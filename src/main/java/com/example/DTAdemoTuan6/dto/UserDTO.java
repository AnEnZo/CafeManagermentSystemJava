package com.example.DTAdemoTuan6.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;

    // Chỉ dùng để bind từ request (POST/PUT), sẽ không bao giờ show ra JSON response
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String email;

}
