package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationRequest;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<?> applyToSubject(@RequestBody ApplicationRequest request) {
        try {
            applicationService.applyToSubject(request);
            return ResponseEntity.ok(new MessageResponse("Candidature soumise avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT')")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(applicationService.getApplicationsByStudent(studentId));
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(applicationService.getApplicationsBySubject(subjectId));
    }

    @PatchMapping("/{applicationId}/accept")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> acceptApplication(@PathVariable Long applicationId,
                                               @RequestBody(required = false) String commentaire) {
        try {
            applicationService.acceptApplication(applicationId, commentaire);
            return ResponseEntity.ok(new MessageResponse("Candidature acceptée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{applicationId}/reject")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> rejectApplication(@PathVariable Long applicationId,
                                               @RequestBody(required = false) String commentaire) {
        try {
            applicationService.rejectApplication(applicationId, commentaire);
            return ResponseEntity.ok(new MessageResponse("Candidature refusée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}