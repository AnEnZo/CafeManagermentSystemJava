package com.example.DTAdemoTuan6.service;


import com.example.DTAdemoTuan6.dto.UserDTO;
import com.example.DTAdemoTuan6.payload.RegisterRequest;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    boolean deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void registerNewUser(RegisterRequest registerRequest);
    List<UserDTO> searchUser(String username);

}
