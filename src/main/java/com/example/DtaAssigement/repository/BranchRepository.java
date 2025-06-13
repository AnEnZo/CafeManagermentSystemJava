package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByName(String name);
    boolean existsByName(String name);
}
