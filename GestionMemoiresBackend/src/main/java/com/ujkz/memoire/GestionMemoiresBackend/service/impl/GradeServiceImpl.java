package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.GradeMemoireDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.GradeMemoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import com.ujkz.memoire.GestionMemoiresBackend.enums.Mention;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.GradeMemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeMemoireRepository gradeMemoireRepository;

    @Autowired
    private DefenseRepository defenseRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public GradeMemoireDTO saveGrade(Long defenseId, GradeMemoireDTO gradeDTO, String userEmail) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée"));

        if (defense.getStatut() != DefenseStatus.TERMINEE) {
            throw new BusinessException("La soutenance n'est pas encore terminée");
        }

        if (gradeMemoireRepository.findByDefense(defense).isPresent()) {
            throw new BusinessException("Une note existe déjà pour cette soutenance");
        }

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        GradeMemoire grade = new GradeMemoire();
        grade.setDefense(defense);
        grade.setNoteFinale(gradeDTO.getNoteFinale());
        grade.setQualiteDocument(gradeDTO.getQualiteDocument());
        grade.setTravailRealise(gradeDTO.getTravailRealise());
        grade.setPresentationOrale(gradeDTO.getPresentationOrale());
        grade.setReponsesQuestions(gradeDTO.getReponsesQuestions());
        grade.setCommentaires(gradeDTO.getCommentaires());

        Integer note = gradeDTO.getNoteFinale();
        if (note != null) {
            if (note >= 16) grade.setMention(Mention.EXCELLENT);
            else if (note >= 14) grade.setMention(Mention.TRES_BIEN);
            else if (note >= 12) grade.setMention(Mention.BIEN);
            else if (note >= 10) grade.setMention(Mention.ASSEZ_BIEN);
            else grade.setMention(Mention.PASSABLE);
        }

        grade.setValidee(true);
        grade.setDateValidation(LocalDateTime.now());
        grade.setValidePar(currentUser);

        GradeMemoire saved = gradeMemoireRepository.save(grade);

        Memoire memoire = defense.getMemoire();
        memoire.setNoteFinale(grade.getNoteFinale());
        memoire.setMention(grade.getMention());
        memoire.setSoutenu(true);
        memoire.setDateSoutenance(defense.getDateHeure());
        memoireRepository.save(memoire);

        return convertToDTO(saved);
    }

    @Override
    public GradeMemoireDTO getGradeByDefense(Long defenseId) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée"));

        GradeMemoire grade = gradeMemoireRepository.findByDefense(defense)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune note trouvée pour cette soutenance"));

        return convertToDTO(grade);
    }

    private GradeMemoireDTO convertToDTO(GradeMemoire grade) {
        GradeMemoireDTO dto = new GradeMemoireDTO();
        dto.setId(grade.getId());
        dto.setDefenseId(grade.getDefense().getId());
        dto.setEtudiantNom(grade.getDefense().getMemoire().getStudent().getUser().getPrenom() + " " +
                          grade.getDefense().getMemoire().getStudent().getUser().getNom());
        dto.setSujetTitre(grade.getDefense().getMemoire().getSubject().getTitre());
        dto.setNoteFinale(grade.getNoteFinale());
        dto.setMention(grade.getMention() != null ? grade.getMention().getLibelle() : null);
        dto.setCommentaires(grade.getCommentaires());
        dto.setQualiteDocument(grade.getQualiteDocument());
        dto.setTravailRealise(grade.getTravailRealise());
        dto.setPresentationOrale(grade.getPresentationOrale());
        dto.setReponsesQuestions(grade.getReponsesQuestions());
        dto.setValidee(grade.getValidee());
        dto.setDateValidation(grade.getDateValidation());
        dto.setValidePar(grade.getValidePar() != null ?
                        grade.getValidePar().getPrenom() + " " + grade.getValidePar().getNom() : null);
        return dto;
    }
}