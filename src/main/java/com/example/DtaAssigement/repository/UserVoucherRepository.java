package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVoucherRepository extends JpaRepository<UserVoucher,Long> {

    Optional<UserVoucher> findByQrCode(String qrCode);
    boolean existsByCode(String code);
    UserVoucher getById(Long id);
    Optional<UserVoucher> findByCode(String code);
}
