package com.example.DtaAssigement.service;

import com.example.DtaAssigement.entity.Shift;
import java.util.List;

public interface ShiftService {
    Shift createShift(Shift shift);
    List<Shift> getAllShifts();
    Shift getShiftById(Long id);
    Shift updateShift(Long id, Shift shift);
    void deleteShift(Long id);
}