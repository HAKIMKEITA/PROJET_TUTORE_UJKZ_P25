package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.MilestoneDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Milestone;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MilestoneRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MilestoneServiceImpl implements MilestoneService {

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<MilestoneDTO> getMilestonesByMemoire(Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));
        
        return milestoneRepository.findByMemoireOrderByOrdreAsc(memoire).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MilestoneDTO> getPendingMilestones(Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));
        
        return milestoneRepository.findByMemoireAndRealiseFalse(memoire).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MilestoneDTO createMilestone(MilestoneDTO milestoneDTO) {
        Memoire memoire = memoireRepository.findById(milestoneDTO.getMemoireId())
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));

        Milestone milestone = new Milestone();
        milestone.setMemoire(memoire);
        milestone.setLibelle(milestoneDTO.getLibelle());
        milestone.setDescription(milestoneDTO.getDescription());
        milestone.setEcheance(milestoneDTO.getEcheance());
        milestone.setRealise(false);
        milestone.setOrdre(milestoneDTO.getOrdre());

        Milestone saved = milestoneRepository.save(milestone);
        return convertToDTO(saved);
    }

    @Override
    public MilestoneDTO completeMilestone(Long milestoneId, String userEmail) {
        Milestone milestone = getMilestoneEntity(milestoneId);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        milestone.setRealise(true);
        milestone.setDateRealisation(LocalDateTime.now());
        milestone.setValidePar(currentUser);

        Milestone saved = milestoneRepository.save(milestone);
        return convertToDTO(saved);
    }

    @Override
    public MilestoneDTO updateMilestone(Long milestoneId, MilestoneDTO milestoneDTO) {
        Milestone milestone = getMilestoneEntity(milestoneId);

        if (milestoneDTO.getLibelle() != null) milestone.setLibelle(milestoneDTO.getLibelle());
        if (milestoneDTO.getDescription() != null) milestone.setDescription(milestoneDTO.getDescription());
        if (milestoneDTO.getEcheance() != null) milestone.setEcheance(milestoneDTO.getEcheance());
        if (milestoneDTO.getOrdre() != null) milestone.setOrdre(milestoneDTO.getOrdre());

        Milestone saved = milestoneRepository.save(milestone);
        return convertToDTO(saved);
    }

    @Override
    public void deleteMilestone(Long milestoneId) {
        Milestone milestone = getMilestoneEntity(milestoneId);
        milestoneRepository.delete(milestone);
    }

    @Override
    public Milestone getMilestoneEntity(Long id) {
        return milestoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jalon non trouvé avec l'ID: " + id));
    }

    private MilestoneDTO convertToDTO(Milestone milestone) {
        MilestoneDTO dto = new MilestoneDTO();
        dto.setId(milestone.getId());
        dto.setMemoireId(milestone.getMemoire().getId());
        dto.setLibelle(milestone.getLibelle());
        dto.setDescription(milestone.getDescription());
        dto.setEcheance(milestone.getEcheance());
        dto.setRealise(milestone.getRealise());
        dto.setDateRealisation(milestone.getDateRealisation());
        dto.setCommentaire(milestone.getCommentaire());
        dto.setOrdre(milestone.getOrdre());
        if (milestone.getValidePar() != null) {
            dto.setValidePar(milestone.getValidePar().getPrenom() + " " + milestone.getValidePar().getNom());
        }
        return dto;
    }
}