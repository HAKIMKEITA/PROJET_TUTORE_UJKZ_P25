package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;

import java.util.List;

public interface DefenseService {
    List<DefenseDTO> getAllDefenses();
    DefenseDTO getDefenseById(Long id);
    DefenseDTO getDefenseByMemoire(Long memoireId);
    List<DefenseDTO> getDefensesBySession(Long sessionId);
    DefenseDTO updateDefenseStatus(Long id, DefenseStatus status);
    DefenseDTO validateDefense(Long id);
    Defense getDefenseEntity(Long id);
}