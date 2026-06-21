package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseSessionDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseSession;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseSessionRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SemesterRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.DefenseSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefenseSessionServiceImpl implements DefenseSessionService {

    @Autowired
    private DefenseSessionRepository defenseSessionRepository;

    @Autowired
    private DefenseRepository defenseRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public List<DefenseSessionDTO> getAllSessions() {
        return defenseSessionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DefenseSessionDTO createSession(DefenseSessionDTO sessionDTO) {
        Semester semester = semesterRepository.findById(sessionDTO.getSemestreId())
                .orElseThrow(() -> new ResourceNotFoundException("Semestre non trouvé"));

        DefenseSession session = new DefenseSession();
        session.setLibelle(sessionDTO.getLibelle());
        session.setDateSession(sessionDTO.getDateSession());
        session.setHeureDebut(sessionDTO.getHeureDebut());
        session.setHeureFin(sessionDTO.getHeureFin());
        session.setSalle(sessionDTO.getSalle());
        session.setSemestre(semester);
        session.setDescription(sessionDTO.getDescription());
        session.setNombreMaxSoutenances(sessionDTO.getNombreMaxSoutenances());
        session.setActive(true);

        DefenseSession saved = defenseSessionRepository.save(session);
        return convertToDTO(saved);
    }

    @Override
    public DefenseSessionDTO getSessionById(Long id) {
        DefenseSession session = getSessionEntity(id);
        return convertToDTO(session);
    }

    @Override
    public DefenseSessionDTO addMemoireToSession(Long sessionId, Long memoireId) {
        DefenseSession session = getSessionEntity(sessionId);
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));

        if (!memoire.getSoutenable()) {
            throw new BusinessException("Ce mémoire n'est pas soutenable");
        }

        if (defenseRepository.findByMemoire(memoire).isPresent()) {
            throw new BusinessException("Ce mémoire est déjà programmé");
        }

        Defense defense = new Defense();
        defense.setMemoire(memoire);
        defense.setSession(session);
        defense.setDateHeure(session.getDateSession());
        defense.setStatut(DefenseStatus.PROGRAMMEE);
        defense.setValidee(false);

        defenseRepository.save(defense);

        memoire.setSoutenu(false);
        memoireRepository.save(memoire);

        return convertToDTO(session);
    }

    @Override
    public DefenseSessionDTO validateSession(Long id) {
        DefenseSession session = getSessionEntity(id);
        session.setActive(false);
        DefenseSession saved = defenseSessionRepository.save(session);
        return convertToDTO(saved);
    }

    @Override
    public DefenseSession getSessionEntity(Long id) {
        return defenseSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée avec l'ID: " + id));
    }

    private DefenseSessionDTO convertToDTO(DefenseSession session) {
        DefenseSessionDTO dto = new DefenseSessionDTO();
        dto.setId(session.getId());
        dto.setLibelle(session.getLibelle());
        dto.setDateSession(session.getDateSession());
        dto.setHeureDebut(session.getHeureDebut());
        dto.setHeureFin(session.getHeureFin());
        dto.setSalle(session.getSalle());
        dto.setSemestreId(session.getSemestre().getId());
        dto.setSemestreLibelle(session.getSemestre().getLibelle());
        dto.setActive(session.getActive());
        dto.setDescription(session.getDescription());
        dto.setNombreMaxSoutenances(session.getNombreMaxSoutenances());

        List<Defense> defenses = defenseRepository.findBySession(session);
        dto.setNombreSoutenancesProgrammees(defenses.size());

        List<DefenseDTO> defenseDTOs = defenses.stream()
                .map(this::convertDefenseToDTO)
                .collect(Collectors.toList());
        dto.setDefenses(defenseDTOs);

        return dto;
    }

    private DefenseDTO convertDefenseToDTO(Defense defense) {
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

        if (defense.getMemoire().getNoteFinale() != null) {
            dto.setNoteFinale(defense.getMemoire().getNoteFinale());
        }
        if (defense.getMemoire().getMention() != null) {
            dto.setMention(defense.getMemoire().getMention().getLibelle());
        }

        return dto;
    }
}