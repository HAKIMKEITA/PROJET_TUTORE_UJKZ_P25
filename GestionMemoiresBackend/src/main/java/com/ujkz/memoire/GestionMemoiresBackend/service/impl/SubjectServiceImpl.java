/*package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.SubjectDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Teacher;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import com.ujkz.memoire.GestionMemoiresBackend.enums.SubjectStatus;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SubjectRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.TeacherRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SemesterRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public List<SubjectDTO> getAllPublishedSubjects() {
        return subjectRepository.findByPublieTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectDTO getSubjectById(Long id) {
        Subject subject = getSubjectEntity(id);
        return convertToDTO(subject);
    }

    @Override
    public List<SubjectDTO> getSubjectsByTeacher(Long teacherId) {
        return subjectRepository.findByEncadrantId(teacherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Subject getSubjectEntity(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet non trouvé avec l'ID: " + id));
    }

    @Override
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {
        Teacher encadrant = teacherRepository.findById(subjectDTO.getEncadrantId())
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant encadrant non trouvé"));
        
        Teacher superviseur = teacherRepository.findById(subjectDTO.getSuperviseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant superviseur non trouvé"));
        
        Semester semester = semesterRepository.findById(subjectDTO.getSemestreId())
                .orElseThrow(() -> new ResourceNotFoundException("Semestre non trouvé"));

        Subject subject = new Subject();
        subject.setTitre(subjectDTO.getTitre());
        subject.setResume(subjectDTO.getResume());
        subject.setObjectifs(subjectDTO.getObjectifs());
        subject.setCompetencesRequises(subjectDTO.getCompetencesRequises());
        subject.setMotsCles(subjectDTO.getMotsCles());
        subject.setEncadrant(encadrant);
        subject.setSuperviseur(superviseur);
        subject.setSemestre(semester);
        subject.setCapaciteMax(subjectDTO.getCapaciteMax() != null ? subjectDTO.getCapaciteMax() : 1);
        subject.setStatut(SubjectStatus.OUVERT);
        subject.setPublie(subjectDTO.getPublie() != null ? subjectDTO.getPublie() : false);

        Subject saved = subjectRepository.save(subject);
        return convertToDTO(saved);
    }

    @Override
    public SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO) {
        Subject subject = getSubjectEntity(id);

        if (subjectDTO.getTitre() != null) subject.setTitre(subjectDTO.getTitre());
        if (subjectDTO.getResume() != null) subject.setResume(subjectDTO.getResume());
        if (subjectDTO.getObjectifs() != null) subject.setObjectifs(subjectDTO.getObjectifs());
        if (subjectDTO.getCompetencesRequises() != null) subject.setCompetencesRequises(subjectDTO.getCompetencesRequises());
        if (subjectDTO.getMotsCles() != null) subject.setMotsCles(subjectDTO.getMotsCles());
        if (subjectDTO.getCapaciteMax() != null) subject.setCapaciteMax(subjectDTO.getCapaciteMax());
        if (subjectDTO.getEncadrantId() != null) {
            Teacher encadrant = teacherRepository.findById(subjectDTO.getEncadrantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant encadrant non trouvé"));
            subject.setEncadrant(encadrant);
        }
        if (subjectDTO.getSuperviseurId() != null) {
            Teacher superviseur = teacherRepository.findById(subjectDTO.getSuperviseurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant superviseur non trouvé"));
            subject.setSuperviseur(superviseur);
        }
        if (subjectDTO.getSemestreId() != null) {
            Semester semester = semesterRepository.findById(subjectDTO.getSemestreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semestre non trouvé"));
            subject.setSemestre(semester);
        }

        Subject updated = subjectRepository.save(subject);
        return convertToDTO(updated);
    }

    @Override
    public void deleteSubject(Long id) {
        Subject subject = getSubjectEntity(id);
        subjectRepository.delete(subject);
    }

    @Override
    public SubjectDTO changeSubjectStatus(Long id, SubjectStatus status) {
        Subject subject = getSubjectEntity(id);
        subject.setStatut(status);
        if (status == SubjectStatus.FERME) {
            // Fermer les candidatures
        }
        Subject updated = subjectRepository.save(subject);
        return convertToDTO(updated);
    }

    @Override
    public SubjectDTO togglePublication(Long id) {
        Subject subject = getSubjectEntity(id);
        subject.setPublie(!subject.isPublie());
        if (subject.isPublie()) {
            subject.setDatePublication(LocalDateTime.now());
        }
        Subject updated = subjectRepository.save(subject);
        return convertToDTO(updated);
    }

    private SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setTitre(subject.getTitre());
        dto.setResume(subject.getResume());
        dto.setObjectifs(subject.getObjectifs());
        dto.setCompetencesRequises(subject.getCompetencesRequises());
        dto.setMotsCles(subject.getMotsCles());
        dto.setEncadrantId(subject.getEncadrant().getId());
        dto.setEncadrantNom(subject.getEncadrant().getUser().getPrenom() + " " + 
                           subject.getEncadrant().getUser().getNom());
        dto.setSuperviseurId(subject.getSuperviseur().getId());
        dto.setSuperviseurNom(subject.getSuperviseur().getUser().getPrenom() + " " + 
                              subject.getSuperviseur().getUser().getNom());
        dto.setSemestreId(subject.getSemestre().getId());
        dto.setSemestreLibelle(subject.getSemestre().getLibelle());
        dto.setCapaciteMax(subject.getCapaciteMax());
        dto.setStatut(subject.getStatut().toString());
        dto.setPublie(subject.isPublie());
        dto.setDateCreation(subject.getDateCreation());
        return dto;
    }
}

*/