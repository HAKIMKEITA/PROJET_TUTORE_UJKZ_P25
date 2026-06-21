package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.MemoireDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Application;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.enums.ApplicationStatus;
import com.ujkz.memoire.GestionMemoiresBackend.enums.AvancementStatus;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.ApplicationRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.MemoireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemoireServiceImpl implements MemoireService {

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public MemoireDTO getMemoireByStudent(Long studentId) {
        Memoire memoire = memoireRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun mémoire trouvé pour cet étudiant"));
        return convertToDTO(memoire);
    }

    @Override
    public MemoireDTO getMemoireBySubject(Long subjectId) {
        Memoire memoire = memoireRepository.findBySubjectId(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun mémoire trouvé pour ce sujet"));
        return convertToDTO(memoire);
    }

    @Override
    public List<MemoireDTO> getAllMemoires() {
        return memoireRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MemoireDTO createMemoireFromApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature non trouvée"));

        if (application.getStatut() != ApplicationStatus.ACCEPTED) {
            throw new BusinessException("Cette candidature n'est pas acceptée");
        }

        if (memoireRepository.findByStudentId(application.getStudent().getId()).isPresent()) {
            throw new BusinessException("Cet étudiant a déjà un mémoire");
        }

        if (memoireRepository.findBySubjectId(application.getSubject().getId()).isPresent()) {
            throw new BusinessException("Ce sujet a déjà un mémoire associé");
        }

        Memoire memoire = new Memoire();
        memoire.setApplication(application);
        memoire.setStudent(application.getStudent());
        memoire.setSubject(application.getSubject());
        memoire.setStatutAvancement(AvancementStatus.EN_COURS);
        memoire.setSoutenable(false);

        Memoire saved = memoireRepository.save(memoire);
        return convertToDTO(saved);
    }

    @Override
    public MemoireDTO updateAvancement(Long memoireId, AvancementStatus statut) {
        Memoire memoire = getMemoireEntity(memoireId);
        memoire.setStatutAvancement(statut);
        Memoire saved = memoireRepository.save(memoire);
        return convertToDTO(saved);
    }

    @Override
    public MemoireDTO validateSoutenabilite(Long memoireId, Boolean soutenable, String commentaire) {
        Memoire memoire = getMemoireEntity(memoireId);
        memoire.setSoutenable(soutenable);
        memoire.setDateValidationSoutenabilite(LocalDateTime.now());
        memoire.setCommentaireSoutenabilite(commentaire);

        if (soutenable) {
            memoire.setStatutAvancement(AvancementStatus.SOUTENABLE);
        }

        Memoire saved = memoireRepository.save(memoire);
        return convertToDTO(saved);
    }

    @Override
    public List<MemoireDTO> getSoutenables() {
        return memoireRepository.findBySoutenableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Memoire getMemoireEntity(Long id) {
        return memoireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé avec l'ID: " + id));
    }

    @Override
    public MemoireStats getStats() {
        long total = memoireRepository.count();
        long soutenables = memoireRepository.countBySoutenableTrue();
        long nonSoutenus = memoireRepository.countBySoutenuFalse();
        return new MemoireStats(total, soutenables, nonSoutenus);
    }

    private MemoireDTO convertToDTO(Memoire memoire) {
        MemoireDTO dto = new MemoireDTO();
        dto.setId(memoire.getId());
        dto.setStudentId(memoire.getStudent().getId());
        dto.setStudentNom(memoire.getStudent().getUser().getNom());
        dto.setStudentPrenom(memoire.getStudent().getUser().getPrenom());
        dto.setStudentMatricule(memoire.getStudent().getMatricule());
        dto.setSubjectId(memoire.getSubject().getId());
        dto.setSubjectTitre(memoire.getSubject().getTitre());
        dto.setEncadrantNom(memoire.getSubject().getEncadrant().getUser().getPrenom() + " " +
                           memoire.getSubject().getEncadrant().getUser().getNom());
        dto.setSuperviseurNom(memoire.getSubject().getSuperviseur().getUser().getPrenom() + " " +
                              memoire.getSubject().getSuperviseur().getUser().getNom());
        dto.setDateAffectation(memoire.getDateAffectation());
        dto.setStatutAvancement(memoire.getStatutAvancement());
        dto.setStatutAvancementLibelle(memoire.getStatutAvancement().getLibelle());
        dto.setSoutenable(memoire.getSoutenable());
        dto.setDateValidationSoutenabilite(memoire.getDateValidationSoutenabilite());
        dto.setCommentaireSoutenabilite(memoire.getCommentaireSoutenabilite());
        dto.setSoutenu(memoire.getSoutenu());
        dto.setNoteFinale(memoire.getNoteFinale());
        dto.setMention(memoire.getMention() != null ? memoire.getMention().getLibelle() : null);
        dto.setDateSoutenance(memoire.getDateSoutenance());
        return dto;
    }
}