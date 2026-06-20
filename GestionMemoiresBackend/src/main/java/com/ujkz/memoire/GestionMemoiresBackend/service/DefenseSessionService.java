package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseSessionDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseSession;

import java.util.List;

public interface DefenseSessionService {
    List<DefenseSessionDTO> getAllSessions();
    DefenseSessionDTO createSession(DefenseSessionDTO sessionDTO);
    DefenseSessionDTO getSessionById(Long id);
    DefenseSessionDTO addMemoireToSession(Long sessionId, Long memoireId);
    DefenseSessionDTO validateSession(Long id);
    DefenseSession getSessionEntity(Long id);
}