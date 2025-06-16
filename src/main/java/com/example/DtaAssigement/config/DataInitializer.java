package com.example.DtaAssigement.config;


import com.example.DtaAssigement.entity.Roles;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.ennum.AuthProvider;
import com.example.DtaAssigement.repository.RolesRepository;
import com.example.DtaAssigement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolesRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Tạo 3 role nếu chưa có
        String[] roles = { "ROLE_ADMIN", "ROLE_STAFF", "ROLE_USER" };
        for (String roleName : roles) {
            if (roleRepo.findByName(roleName) == null) {
                roleRepo.save(Roles.builder()
                        .name(roleName)
                        .build());
            }
        }

        // 2. Tạo user admin nếu chưa có
        if (userRepo.findByUsername("admin") == null) {
            User admin = User.builder()
                    .username("admin")
                    .displayName("AnEnZo")
                    .password(passwordEncoder.encode("123456"))
                    .email("dinhtuanan2802@gmail.com")
                    .phoneNumber("84564870803")
                    .rewardPoints(0)
                    .provider(AuthProvider.LOCAL)
                    .build();

            // Gán ROLE_ADMIN cho admin
            Roles adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Error: Role ADMIN không tồn tại"));
            Set<Roles> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);

            userRepo.save(admin);
        }
    }
}

