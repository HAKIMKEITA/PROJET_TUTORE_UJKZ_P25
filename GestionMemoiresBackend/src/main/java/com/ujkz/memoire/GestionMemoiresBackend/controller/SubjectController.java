package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.SubjectDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SubjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    // GET tous les sujets publiés
    @Operation(summary = "Voir la liste des sujets publies")
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllPublishedSubjects() {
        List<Subject> subjects = subjectRepository.findByPublieTrue();
        List<SubjectDTO> subjectDTOs = subjects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }

    // GET sujet par ID
    @Operation(summary = "Obtenir les details d'un sujet")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé avec l'id: " + id));
        return ResponseEntity.ok(convertToDTO(subject));
    }

    // GET sujets par enseignant
    @Operation(summary = "Obtenir la liste des sujets par enseignant")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByTeacher(@PathVariable Long teacherId) {
        List<Subject> subjects = subjectRepository.findByEncadrantId(teacherId);
        List<SubjectDTO> subjectDTOs = subjects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }

    private SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setTitre(subject.getTitre());
        dto.setResume(subject.getResume());
        dto.setObjectifs(subject.getObjectifs());
        dto.setCompetencesRequises(subject.getCompetencesRequises());
        dto.setMotsCles(subject.getMotsCles());
        dto.setEncadrantNom(subject.getEncadrant().getUser().getPrenom() + " " + subject.getEncadrant().getUser().getNom());
        dto.setSuperviseurNom(subject.getSuperviseur().getUser().getPrenom() + " " + subject.getSuperviseur().getUser().getNom());
        dto.setSemestreLibelle(subject.getSemestre().getLibelle());
        dto.setCapaciteMax(subject.getCapaciteMax());
        dto.setStatut(subject.getStatut().toString());
        dto.setPublie(subject.isPublie());
        dto.setDateCreation(subject.getDateCreation());
        return dto;
    }
}