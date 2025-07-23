package com.example.DtaAssigement.service;


import com.example.DtaAssigement.dto.UserDTO;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.payload.RegisterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO createStaff(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    boolean deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void registerNewUser(RegisterRequest registerRequest);
    List<UserDTO> searchUser(String username);
    Optional<User> findByEmail(String email);
    void updatePassword(User user, String rawPassword);
    Optional<User> findByUsername(String username);
    User processOAuthUser(String provider, String providerId, String email, String username, String displayname);
    UserDTO getUserByUsername(String username);
    UserDTO updateCurrentUser(String username, Map<String,Object> updates);
}
