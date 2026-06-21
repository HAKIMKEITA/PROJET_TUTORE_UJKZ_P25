package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.dto.ObservationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Observation;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.ObservationType;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.ObservationRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/observations")
@CrossOrigin(origins = "*")
public class ObservationController {

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private UserRepository userRepository;

    // GET - Récupérer toutes les observations d'un mémoire
    @GetMapping("/memoire/{memoireId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<ObservationDTO>> getObservationsByMemoire(@PathVariable Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        List<Observation> observations = observationRepository.findByMemoireOrderByDateObservationDesc(memoire);
        List<ObservationDTO> dtos = observations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Récupérer les observations non vues par l'étudiant
    @GetMapping("/memoire/{memoireId}/unread")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<ObservationDTO>> getUnreadObservations(@PathVariable Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        List<Observation> observations = observationRepository.findByMemoireAndVuParEtudiantFalse(memoire);
        List<ObservationDTO> dtos = observations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST - Créer une observation
    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> createObservation(@RequestBody ObservationDTO observationDTO) {
        Memoire memoire = memoireRepository.findById(observationDTO.getMemoireId())
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est l'encadrant ou un admin
        boolean isEncadrant = memoire.getSubject().getEncadrant().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");

        if (!isEncadrant && !isAdmin) {
            return ResponseEntity.status(403).body(new MessageResponse("Seul l'encadrant peut créer des observations"));
        }

        Observation observation = new Observation();
        observation.setMemoire(memoire);
        observation.setAuteur(currentUser);
        observation.setContenu(observationDTO.getContenu());
        observation.setTypeObservation(observationDTO.getTypeObservation());
        observation.setVuParEtudiant(false);

        observationRepository.save(observation);

        return ResponseEntity.ok(new MessageResponse("Observation créée avec succès"));
    }

    // PATCH - Marquer une observation comme vue
    @PatchMapping("/{observationId}/mark-read")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> markObservationAsRead(@PathVariable Long observationId) {
        Observation observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new RuntimeException("Observation non trouvée"));

        // Seul l'étudiant peut marquer comme vue
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isStudent = observation.getMemoire().getStudent().getUser().getId().equals(currentUser.getId());
        if (!isStudent) {
            return ResponseEntity.status(403).body(new MessageResponse("Seul l'étudiant peut marquer une observation comme vue"));
        }

        observation.setVuParEtudiant(true);
        observation.setDateVu(LocalDateTime.now());
        observationRepository.save(observation);

        return ResponseEntity.ok(new MessageResponse("Observation marquée comme vue"));
    }

    // DELETE - Supprimer une observation
    @DeleteMapping("/{observationId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> deleteObservation(@PathVariable Long observationId) {
        Observation observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new RuntimeException("Observation non trouvée"));

        observationRepository.delete(observation);
        return ResponseEntity.ok(new MessageResponse("Observation supprimée avec succès"));
    }

    private ObservationDTO convertToDTO(Observation observation) {
        ObservationDTO dto = new ObservationDTO();
        dto.setId(observation.getId());
        dto.setMemoireId(observation.getMemoire().getId());
        dto.setAuteurId(observation.getAuteur().getId());
        dto.setAuteurNom(observation.getAuteur().getPrenom() + " " + observation.getAuteur().getNom());
        dto.setAuteurRole(observation.getAuteur().getRole().name());
        dto.setContenu(observation.getContenu());
        dto.setDateObservation(observation.getDateObservation());
        dto.setTypeObservation(observation.getTypeObservation());
        dto.setTypeObservationLibelle(observation.getTypeObservation() != null ? 
                                     observation.getTypeObservation().getLibelle() : null);
        dto.setVuParEtudiant(observation.getVuParEtudiant());
        dto.setDateVu(observation.getDateVu());
        return dto;
    }
}