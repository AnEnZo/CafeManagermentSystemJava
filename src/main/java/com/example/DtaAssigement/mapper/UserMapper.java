package com.example.DtaAssigement.mapper;


import com.example.DtaAssigement.dto.UserDTO;
import com.example.DtaAssigement.entity.Roles;
import com.example.DtaAssigement.entity.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .displayName(user.getDisplayName())
                .rewardPoints(user.getRewardPoints())
                .roles(user.getRoles().stream()
                        .map(Roles::getName)
                        .collect(Collectors.toSet()))
                .provider(user.getProvider().name())
                .providerId(user.getProviderId())
                .build();
    }

    public static User toEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .displayName(dto.getDisplayName())
                .build();
    }
}
