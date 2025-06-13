package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.Branch;

import java.util.List;

public interface BranchService {

    List<Branch> getAllBranches();
    Branch getBranchById(Long id);
    Branch createBranch(Branch branch);
    Branch updateBranch(Long id, Branch branch);
    boolean deleteBranch(Long id);

}
