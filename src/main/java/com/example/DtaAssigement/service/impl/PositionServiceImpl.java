package com.example.DtaAssigement.service.impl;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.DtaAssigement.entity.Position;
import com.example.DtaAssigement.repository.PositionRepository;
import com.example.DtaAssigement.service.PositionService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@Transactional
@AllArgsConstructor
public class PositionServiceImpl implements PositionService {

    private PositionRepository repository;

    @Override
    public List<Position> getAllPositions() {
        return repository.findAll();
    }

    @Override
    public Position getPositionById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Position not found with id " + id));
    }

    @Override
    public Position createPosition(Position position) {
        if (repository.existsByName(position.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Position name already exists");
        }
        return repository.save(position);
    }

    @Override
    public Position updatePosition(Long id, Position position) {
        Position existing = getPositionById(id);
        if (!existing.getName().equals(position.getName())
                && repository.existsByName(position.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Position name already exists");
        }
        existing.setName(position.getName());
        return repository.save(existing);
    }

    @Override
    public void deletePosition(Long id) {
        Position existing = getPositionById(id);
        repository.delete(existing);
    }
}