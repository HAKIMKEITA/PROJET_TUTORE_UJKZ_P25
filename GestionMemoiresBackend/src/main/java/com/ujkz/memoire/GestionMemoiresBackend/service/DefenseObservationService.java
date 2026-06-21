package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseObservationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseObservation;

import java.util.List;

public interface DefenseObservationService {
    List<DefenseObservationDTO> getObservationsByDefense(Long defenseId);
    DefenseObservationDTO createObservation(DefenseObservationDTO observationDTO, String userEmail);
    void deleteObservation(Long id, String userEmail);
    DefenseObservation getObservationEntity(Long id);
}