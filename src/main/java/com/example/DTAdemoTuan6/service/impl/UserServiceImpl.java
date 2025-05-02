package com.example.DTAdemoTuan6.service.impl;



import com.example.DTAdemoTuan6.dto.UserDTO;
import com.example.DTAdemoTuan6.entity.User;
import com.example.DTAdemoTuan6.mapper.UserMapper;
import com.example.DTAdemoTuan6.payload.RegisterRequest;
import com.example.DTAdemoTuan6.repository.RolesRepository;
import com.example.DTAdemoTuan6.repository.UserRepository;
import com.example.DTAdemoTuan6.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.DTAdemoTuan6.entity.Roles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptpasswordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO)
                .orElse(null);
    }

    public UserDTO createUser(UserDTO userDTO) {
        // Mã hóa mật khẩu
        String encodedPassword = bcryptpasswordEncoder.encode(userDTO.getPassword());
        Roles userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER không tồn tại"));

        userDTO.setPassword(encodedPassword);

        User user = UserMapper.toEntity(userDTO);
        user.setRoles(Set.of(userRole));
        return UserMapper.toDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {

        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;

        // Mã hóa mật khẩu
        String encodedPassword = bcryptpasswordEncoder.encode(userDTO.getPassword());

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodedPassword);
        return UserMapper.toDTO(userRepository.save(user));
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }
    @Override
    public List<UserDTO> searchUser(String keyword){
        return userRepository.findByUsernameContainingIgnoreCase(keyword).stream()
                        .map(UserMapper::toDTO)
                        .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username){
        return userRepository.findByUsername(username).isPresent();
    }
    @Override
    public boolean existsByEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    // Đăng ký người dùng mới
    @Override
    public void registerNewUser(RegisterRequest registerRequest) {
        // Mã hóa mật khẩu
        String encodedPassword = bcryptpasswordEncoder.encode(registerRequest.getPassword());
        Roles userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER không tồn tại"));

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(encodedPassword)
                .email(registerRequest.getEmail())
                .roles(Set.of(userRole)) // gán role mặc định
                .build();

        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);
    }


}
