package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.MilestoneDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Milestone;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MilestoneRepository;
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
@RequestMapping("/milestones")
@CrossOrigin(origins = "*")
public class MilestoneController {

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private UserRepository userRepository;

    // GET - Récupérer tous les jalons d'un mémoire
    @GetMapping("/memoire/{memoireId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByMemoire(@PathVariable Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        List<Milestone> milestones = milestoneRepository.findByMemoireOrderByOrdreAsc(memoire);
        List<MilestoneDTO> dtos = milestones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Récupérer les jalons non réalisés
    @GetMapping("/memoire/{memoireId}/pending")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<MilestoneDTO>> getPendingMilestones(@PathVariable Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        List<Milestone> milestones = milestoneRepository.findByMemoireAndRealiseFalse(memoire);
        List<MilestoneDTO> dtos = milestones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST - Créer un jalon
    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> createMilestone(@RequestBody MilestoneDTO milestoneDTO) {
        Memoire memoire = memoireRepository.findById(milestoneDTO.getMemoireId())
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        Milestone milestone = new Milestone();
        milestone.setMemoire(memoire);
        milestone.setLibelle(milestoneDTO.getLibelle());
        milestone.setDescription(milestoneDTO.getDescription());
        milestone.setEcheance(milestoneDTO.getEcheance());
        milestone.setRealise(false);
        milestone.setOrdre(milestoneDTO.getOrdre());

        milestoneRepository.save(milestone);

        return ResponseEntity.ok(new MessageResponse("Jalon créé avec succès"));
    }

    // PATCH - Marquer un jalon comme réalisé
    @PatchMapping("/{milestoneId}/complete")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> completeMilestone(@PathVariable Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Jalon non trouvé"));

        milestone.setRealise(true);
        milestone.setDateRealisation(LocalDateTime.now());

        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        milestone.setValidePar(currentUser);

        milestoneRepository.save(milestone);

        return ResponseEntity.ok(new MessageResponse("Jalon marqué comme réalisé avec succès"));
    }

    // PATCH - Mettre à jour un jalon
    @PatchMapping("/{milestoneId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> updateMilestone(@PathVariable Long milestoneId,
                                             @RequestBody MilestoneDTO milestoneDTO) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Jalon non trouvé"));

        if (milestoneDTO.getLibelle() != null) {
            milestone.setLibelle(milestoneDTO.getLibelle());
        }
        if (milestoneDTO.getDescription() != null) {
            milestone.setDescription(milestoneDTO.getDescription());
        }
        if (milestoneDTO.getEcheance() != null) {
            milestone.setEcheance(milestoneDTO.getEcheance());
        }
        if (milestoneDTO.getOrdre() != null) {
            milestone.setOrdre(milestoneDTO.getOrdre());
        }

        milestoneRepository.save(milestone);

        return ResponseEntity.ok(new MessageResponse("Jalon mis à jour avec succès"));
    }

    // DELETE - Supprimer un jalon
    @DeleteMapping("/{milestoneId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> deleteMilestone(@PathVariable Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Jalon non trouvé"));

        milestoneRepository.delete(milestone);
        return ResponseEntity.ok(new MessageResponse("Jalon supprimé avec succès"));
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