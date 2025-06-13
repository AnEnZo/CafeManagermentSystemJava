package com.example.DtaAssigement.service.impl;



import com.example.DtaAssigement.dto.UserDTO;
import com.example.DtaAssigement.ennum.AuthProvider;
import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.mapper.UserMapper;
import com.example.DtaAssigement.payload.RegisterRequest;
import com.example.DtaAssigement.repository.BranchRepository;
import com.example.DtaAssigement.repository.RolesRepository;
import com.example.DtaAssigement.repository.UserRepository;
import com.example.DtaAssigement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.DtaAssigement.entity.Roles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RolesRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private BranchRepository branchRepo;


    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toDTO);
    }


    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO)
                .orElse(null);
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username đã có người sử dụng");
        }
        if (existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã có người sử dụng");
        }
        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        Roles userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER không tồn tại"));

        User user = UserMapper.toEntity(userDTO);
        user.setRoles(Set.of(userRole));
        user.setPassword(encodedPassword);
        user.setRewardPoints(0);
        user.setBranch(null);
        user.setProvider(AuthProvider.LOCAL);
        return UserMapper.toDTO(userRepository.save(user));
    }

    public UserDTO createStaff(UserDTO userDTO, Long branchID){
        if (existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username đã có người sử dụng");
        }
        if (existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã có người sử dụng");
        }
        Roles StaffRole = roleRepository.findByName("ROLE_STAFF")
                .orElseThrow(() -> new RuntimeException("Error: Role STAFF không tồn tại"));
        Branch StaffBranch = branchRepo.findById(branchID)
                .orElseThrow(() -> new RuntimeException("Error: Branch không tồn tại"));
        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User user = UserMapper.toEntity(userDTO);
        user.setRoles(Set.of(StaffRole));
        user.setPassword(encodedPassword);
        user.setBranch(StaffBranch);
        user.setProvider(AuthProvider.LOCAL);
        return UserMapper.toDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO, Long branchID) {

        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        Branch branch = branchRepo.findById(branchID)
                .orElseThrow(() -> new RuntimeException("Error: Branch không tồn tại"));

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setPassword(encodedPassword);
        user.setBranch(branch);
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

    @Override
    public void registerNewUser(RegisterRequest registerRequest) {
        if (existsByUsername(registerRequest.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username đã có người sử dụng");
        }
        if (existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email đã có người sử dụng");
        }
        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        Roles userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER không tồn tại"));

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(encodedPassword)
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .rewardPoints(0)
                .roles(Set.of(userRole)) // gán role mặc định
                .branch(null)
                .provider(AuthProvider.LOCAL)
                .build();

        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }


    @Override
    public User processOAuthUser(String provider, String providerId,
                                 String email, String username, String displayname) {
        Roles userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER không tồn tại"));

        Optional<User> optional = userRepository.findByEmail(email);
        if (optional.isPresent()) {
            User exist = optional.get();
            if (exist.getProvider() != AuthProvider.valueOf(provider.toUpperCase())) {
                throw new IllegalArgumentException(
                        "Looks like you're signed up with " + exist.getProvider());
            }
            // update info if cần
            return exist;
        } else {
            User u = User.builder()
                    .email(email)
                    .displayName(displayname)
                    .username(username)
                    .provider(AuthProvider.valueOf(provider.toUpperCase()))
                    .providerId(providerId)
                    .rewardPoints(0)
                    .roles(Set.of(userRole))
                    .build();
            return userRepository.save(u);
        }
    }



}
