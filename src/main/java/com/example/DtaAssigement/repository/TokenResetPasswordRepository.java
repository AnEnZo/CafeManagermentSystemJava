package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.TokenResetPassword;
import com.example.DtaAssigement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenResetPasswordRepository extends JpaRepository<TokenResetPassword, Long> {
    Optional<TokenResetPassword> findByToken(String token);
    @Modifying
    @Query("DELETE FROM TokenResetPassword p WHERE p.user = :user")
    void deleteTokensByUser(@Param("user") User user);
}
