package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseSession;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseSessionRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.DefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefenseServiceImpl implements DefenseService {

    @Autowired
    private DefenseRepository defenseRepository;

    @Autowired
    private DefenseSessionRepository defenseSessionRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Override
    public List<DefenseDTO> getAllDefenses() {
        return defenseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DefenseDTO getDefenseById(Long id) {
        Defense defense = getDefenseEntity(id);
        return convertToDTO(defense);
    }

    @Override
    public DefenseDTO getDefenseByMemoire(Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));
        
        Defense defense = defenseRepository.findByMemoire(memoire)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune soutenance trouvée pour ce mémoire"));
        
        return convertToDTO(defense);
    }

    @Override
    public List<DefenseDTO> getDefensesBySession(Long sessionId) {
        DefenseSession session = defenseSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));
        
        return defenseRepository.findBySession(session).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DefenseDTO updateDefenseStatus(Long id, DefenseStatus status) {
        Defense defense = getDefenseEntity(id);
        defense.setStatut(status);
        Defense saved = defenseRepository.save(defense);
        return convertToDTO(saved);
    }

    @Override
    public DefenseDTO validateDefense(Long id) {
        Defense defense = getDefenseEntity(id);
        defense.setValidee(true);
        Defense saved = defenseRepository.save(defense);
        return convertToDTO(saved);
    }

    @Override
    public Defense getDefenseEntity(Long id) {
        return defenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée avec l'ID: " + id));
    }

    private DefenseDTO convertToDTO(Defense defense) {
        DefenseDTO dto = new DefenseDTO();
        dto.setId(defense.getId());
        dto.setMemoireId(defense.getMemoire().getId());
        dto.setEtudiantNom(defense.getMemoire().getStudent().getUser().getPrenom() + " " +
                          defense.getMemoire().getStudent().getUser().getNom());
        dto.setSujetTitre(defense.getMemoire().getSubject().getTitre());
        dto.setSessionId(defense.getSession().getId());
        dto.setSessionLibelle(defense.getSession().getLibelle());
        dto.setDateHeure(defense.getDateHeure());
        dto.setStatut(defense.getStatut());
        dto.setStatutLibelle(defense.getStatut().getLibelle());
        dto.setValidee(defense.getValidee());
        dto.setDureeMinutes(defense.getDureeMinutes());
        dto.setLienVisio(defense.getLienVisio());
        dto.setRapport(defense.getRapport());

        if (defense.getMemoire().getNoteFinale() != null) {
            dto.setNoteFinale(defense.getMemoire().getNoteFinale());
        }
        if (defense.getMemoire().getMention() != null) {
            dto.setMention(defense.getMemoire().getMention().getLibelle());
        }

        return dto;
    }
}