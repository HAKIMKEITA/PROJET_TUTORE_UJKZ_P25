package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseObservationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseObservation;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseObservationRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.DefenseObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefenseObservationServiceImpl implements DefenseObservationService {

    @Autowired
    private DefenseObservationRepository defenseObservationRepository;

    @Autowired
    private DefenseRepository defenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<DefenseObservationDTO> getObservationsByDefense(Long defenseId) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée"));
        
        return defenseObservationRepository.findByDefenseOrderByDateObservationDesc(defense).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DefenseObservationDTO createObservation(DefenseObservationDTO observationDTO, String userEmail) {
        Defense defense = defenseRepository.findById(observationDTO.getDefenseId())
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée"));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier les droits - seul un membre du jury ou admin peut créer une observation
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");
        boolean isTeacher = currentUser.getRole().name().equals("ENSEIGNANT");

        if (!isAdmin && !isTeacher) {
            throw new BusinessException("Seul un enseignant ou administrateur peut créer des observations de soutenance");
        }

        DefenseObservation observation = new DefenseObservation();
        observation.setDefense(defense);
        observation.setAuteur(currentUser);
        observation.setContenu(observationDTO.getContenu());
        observation.setCategorie(observationDTO.getCategorie());

        DefenseObservation saved = defenseObservationRepository.save(observation);
        return convertToDTO(saved);
    }

    @Override
    public void deleteObservation(Long id, String userEmail) {
        DefenseObservation observation = getObservationEntity(id);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        boolean isAuthor = observation.getAuteur().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");

        if (!isAuthor && !isAdmin) {
            throw new BusinessException("Vous n'avez pas le droit de supprimer cette observation");
        }

        defenseObservationRepository.delete(observation);
    }

    @Override
    public DefenseObservation getObservationEntity(Long id) {
        return defenseObservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observation de soutenance non trouvée avec l'ID: " + id));
    }

    private DefenseObservationDTO convertToDTO(DefenseObservation observation) {
        DefenseObservationDTO dto = new DefenseObservationDTO();
        dto.setId(observation.getId());
        dto.setDefenseId(observation.getDefense().getId());
        dto.setAuteurId(observation.getAuteur().getId());
        dto.setAuteurNom(observation.getAuteur().getPrenom() + " " + observation.getAuteur().getNom());
        dto.setContenu(observation.getContenu());
        dto.setCategorie(observation.getCategorie());
        dto.setDateObservation(observation.getDateObservation());
        return dto;
    }
}