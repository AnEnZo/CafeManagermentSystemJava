package com.example.DtaAssigement.repository;


import com.example.DtaAssigement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findByNameAndBranchId(String name, Long branchId);
    Optional<Category> findByNameAndBranchName(String name, String branchName);
}
