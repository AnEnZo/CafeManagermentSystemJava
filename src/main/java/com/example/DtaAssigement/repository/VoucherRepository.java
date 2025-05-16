package com.example.DtaAssigement.repository;


import com.example.DtaAssigement.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Optional<Voucher> findByCode(String code);
}
