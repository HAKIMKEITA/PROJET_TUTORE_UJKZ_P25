package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.ObservationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Observation;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.ObservationRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.ObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ObservationServiceImpl implements ObservationService {

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ObservationDTO> getObservationsByMemoire(Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));
        
        return observationRepository.findByMemoireOrderByDateObservationDesc(memoire).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ObservationDTO> getUnreadObservations(Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));
        
        return observationRepository.findByMemoireAndVuParEtudiantFalse(memoire).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ObservationDTO createObservation(ObservationDTO observationDTO, String userEmail) {
        Memoire memoire = memoireRepository.findById(observationDTO.getMemoireId())
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est l'encadrant ou un admin
        boolean isEncadrant = memoire.getSubject().getEncadrant().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");

        if (!isEncadrant && !isAdmin) {
            throw new BusinessException("Seul l'encadrant peut créer des observations");
        }

        Observation observation = new Observation();
        observation.setMemoire(memoire);
        observation.setAuteur(currentUser);
        observation.setContenu(observationDTO.getContenu());
        observation.setTypeObservation(observationDTO.getTypeObservation());
        observation.setVuParEtudiant(false);

        Observation saved = observationRepository.save(observation);
        return convertToDTO(saved);
    }

    @Override
    public ObservationDTO markObservationAsRead(Long observationId, String userEmail) {
        Observation observation = getObservationEntity(observationId);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        boolean isStudent = observation.getMemoire().getStudent().getUser().getId().equals(currentUser.getId());
        if (!isStudent) {
            throw new BusinessException("Seul l'étudiant peut marquer une observation comme vue");
        }

        observation.setVuParEtudiant(true);
        observation.setDateVu(LocalDateTime.now());

        Observation saved = observationRepository.save(observation);
        return convertToDTO(saved);
    }

    @Override
    public void deleteObservation(Long observationId, String userEmail) {
        Observation observation = getObservationEntity(observationId);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        boolean isEncadrant = observation.getMemoire().getSubject().getEncadrant().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");

        if (!isEncadrant && !isAdmin) {
            throw new BusinessException("Vous n'avez pas le droit de supprimer cette observation");
        }

        observationRepository.delete(observation);
    }

    @Override
    public Observation getObservationEntity(Long id) {
        return observationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observation non trouvée avec l'ID: " + id));
    }

    private ObservationDTO convertToDTO(Observation observation) {
        ObservationDTO dto = new ObservationDTO();
        dto.setId(observation.getId());
        dto.setMemoireId(observation.getMemoire().getId());
        dto.setAuteurId(observation.getAuteur().getId());
        dto.setAuteurNom(observation.getAuteur().getPrenom() + " " + observation.getAuteur().getNom());
        dto.setAuteurRole(observation.getAuteur().getRole().name());
        dto.setContenu(observation.getContenu());
        dto.setDateObservation(observation.getDateObservation());
        dto.setTypeObservation(observation.getTypeObservation());
        dto.setTypeObservationLibelle(observation.getTypeObservation() != null ? 
                                     observation.getTypeObservation().getLibelle() : null);
        dto.setVuParEtudiant(observation.getVuParEtudiant());
        dto.setDateVu(observation.getDateVu());
        return dto;
    }
}