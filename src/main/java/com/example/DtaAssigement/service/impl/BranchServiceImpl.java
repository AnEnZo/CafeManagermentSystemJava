package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.repository.BranchRepository;
import com.example.DtaAssigement.service.BranchService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BranchServiceImpl implements BranchService {


    private BranchRepository branchRepository;

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Branch getBranchById(Long id) {
        return branchRepository.findById(id).orElse(null);
    }

    public Branch createBranch(Branch branch) {
        if (branchRepository.existsByName(branch.getName())) {
            throw new IllegalArgumentException("Tên chi nhánh đã tồn tại");
        }
        return branchRepository.save(branch);
    }

    public Branch updateBranch(Long id, Branch updatedBranch) {
        Branch existingBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chi nhánh không tồn tại"));

        if (branchRepository.existsByName(updatedBranch.getName()) &&
                !existingBranch.getName().equals(updatedBranch.getName())) {
            throw new IllegalArgumentException("Tên chi nhánh đã tồn tại");
        }

        existingBranch.setName(updatedBranch.getName());
        existingBranch.setAddress(updatedBranch.getAddress());
        existingBranch.setPhone(updatedBranch.getPhone());

        return branchRepository.save(existingBranch);
    }


    public boolean deleteBranch(Long id) {
        if (branchRepository.existsById(id)) {
            branchRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
