package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationRequest;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Application;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.enums.ApplicationStatus;
import com.ujkz.memoire.GestionMemoiresBackend.enums.SubjectStatus;
import com.ujkz.memoire.GestionMemoiresBackend.repository.ApplicationRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.StudentRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SubjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;

    // POST - Étudiant postule à un sujet
    @Operation(summary = "Candidater à un sujet")
    @PostMapping("/apply")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<?> applyToSubject(@RequestBody ApplicationRequest request) {
        // Vérifier si l'étudiant existe
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        
        // Vérifier si le sujet existe
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé"));
        
        // Vérifier si le sujet est publié et ouvert
        if (!subject.isPublie() || subject.getStatut() != SubjectStatus.OUVERT) {
            return ResponseEntity.badRequest().body(new MessageResponse("Ce sujet n'est pas disponible"));
        }
        
        // Vérifier si l'étudiant a déjà une candidature en attente ou acceptée
        boolean hasPendingOrAccepted = applicationRepository.existsByStudentAndStatut(student, ApplicationStatus.PENDING) ||
                                       applicationRepository.existsByStudentAndStatut(student, ApplicationStatus.ACCEPTED);
        if (hasPendingOrAccepted) {
            return ResponseEntity.badRequest().body(new MessageResponse("Vous avez déjà une candidature en attente ou acceptée"));
        }
        
        // Vérifier si le sujet est complet
        long candidaturesActuelles = applicationRepository.countBySubjectAndStatut(subject, ApplicationStatus.ACCEPTED);
        if (candidaturesActuelles >= subject.getCapaciteMax()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Ce sujet a atteint sa capacité maximale"));
        }
        
        // Créer la candidature
        Application application = new Application();
        application.setStudent(student);
        application.setSubject(subject);
        application.setMotivation(request.getMotivation());
        application.setStatut(ApplicationStatus.PENDING);
        
        applicationRepository.save(application);
        
        return ResponseEntity.ok(new MessageResponse("Candidature soumise avec succès"));
    }

    // GET - Récupérer les candidatures d'un étudiant
    @Operation(summary = "Récupérer les candidatures d'un étudiant")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ETUDIANT') or hasRole('ENSEIGNANT')")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByStudent(@PathVariable Long studentId) {
        List<Application> applications = applicationRepository.findByStudentId(studentId);
        List<ApplicationDTO> dtos = applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Récupérer les candidatures d'un sujet
    @Operation(summary = "Récupérer les candidatures d'un sujet")
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsBySubject(@PathVariable Long subjectId) {
        List<Application> applications = applicationRepository.findBySubjectId(subjectId);
        List<ApplicationDTO> dtos = applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // PATCH - Accepter une candidature
    @Operation(summary = "Accepter une candidature")
    @PatchMapping("/{applicationId}/accept")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> acceptApplication(@PathVariable Long applicationId,
                                               @RequestBody(required = false) String commentaire) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
        
        if (application.getStatut() != ApplicationStatus.PENDING) {
            return ResponseEntity.badRequest().body(new MessageResponse("Cette candidature n'est plus en attente"));
        }
        
        // Vérifier si le sujet n'est pas complet
        Subject subject = application.getSubject();
        long nbAcceptes = applicationRepository.countBySubjectAndStatut(subject, ApplicationStatus.ACCEPTED);
        if (nbAcceptes >= subject.getCapaciteMax()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Ce sujet a atteint sa capacité maximale"));
        }
        
        application.setStatut(ApplicationStatus.ACCEPTED);
        application.setCommentaireEncadrant(commentaire);
        applicationRepository.save(application);
        
        // Si le sujet est complet, fermer les candidatures
        if (nbAcceptes + 1 >= subject.getCapaciteMax()) {
            subject.setStatut(SubjectStatus.FERME);
            subjectRepository.save(subject);
        }
        
        return ResponseEntity.ok(new MessageResponse("Candidature acceptée avec succès"));
    }

    // PATCH - Refuser une candidature
    @Operation(summary = "Refuser une candidature")
    @PatchMapping("/{applicationId}/reject")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> rejectApplication(@PathVariable Long applicationId,
                                               @RequestBody(required = false) String commentaire) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
        
        if (application.getStatut() != ApplicationStatus.PENDING) {
            return ResponseEntity.badRequest().body(new MessageResponse("Cette candidature n'est plus en attente"));
        }
        
        application.setStatut(ApplicationStatus.REJECTED);
        application.setCommentaireEncadrant(commentaire);
        applicationRepository.save(application);
        
        return ResponseEntity.ok(new MessageResponse("Candidature refusée"));
    }

    private ApplicationDTO convertToDTO(Application application) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(application.getId());
        dto.setStudentId(application.getStudent().getId());
        dto.setStudentNom(application.getStudent().getUser().getNom());
        dto.setStudentPrenom(application.getStudent().getUser().getPrenom());
        dto.setStudentMatricule(application.getStudent().getMatricule());
        dto.setSubjectId(application.getSubject().getId());
        dto.setSubjectTitre(application.getSubject().getTitre());
        dto.setMotivation(application.getMotivation());
        dto.setStatut(application.getStatut());
        dto.setCommentaireEncadrant(application.getCommentaireEncadrant());
        dto.setDateCandidature(application.getDateCandidature());
        return dto;
    }
}