package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {

        Optional<Roles> findByName(String name);
}
