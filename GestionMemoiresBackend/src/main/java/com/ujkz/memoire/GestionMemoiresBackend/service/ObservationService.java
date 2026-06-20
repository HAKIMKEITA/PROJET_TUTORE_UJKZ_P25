package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.ObservationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Observation;

import java.util.List;

public interface ObservationService {
    List<ObservationDTO> getObservationsByMemoire(Long memoireId);
    List<ObservationDTO> getUnreadObservations(Long memoireId);
    ObservationDTO createObservation(ObservationDTO observationDTO, String userEmail);
    ObservationDTO markObservationAsRead(Long observationId, String userEmail);
    void deleteObservation(Long observationId, String userEmail);
    Observation getObservationEntity(Long id);
}