package com.example.DtaAssigement.service;

import java.util.List;
import com.example.DtaAssigement.entity.Position;

public interface PositionService {
    List<Position> getAllPositions();
    Position getPositionById(Long id);
    Position createPosition(Position position);
    Position updatePosition(Long id, Position position);
    void deletePosition(Long id);
}

