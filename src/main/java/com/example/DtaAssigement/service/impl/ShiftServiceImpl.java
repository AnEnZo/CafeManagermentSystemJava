package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.Shift;
import com.example.DtaAssigement.repository.ShiftRepository;
import com.example.DtaAssigement.service.ShiftService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {
    private final ShiftRepository shiftRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public Shift createShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Shift getShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id " + id));
    }

    @Override
    public Shift updateShift(Long id, Shift shift) {
        Shift existing = getShiftById(id);
        existing.setName(shift.getName());
        existing.setStartTime(shift.getStartTime());
        existing.setEndTime(shift.getEndTime());
        return shiftRepository.save(existing);
    }

    @Override
    public void deleteShift(Long id) {
        Shift existing = getShiftById(id);
        shiftRepository.delete(existing);
    }
}