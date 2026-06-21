package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.MemoireDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Application;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.enums.AvancementStatus;
import com.ujkz.memoire.GestionMemoiresBackend.repository.ApplicationRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/memoires")
@CrossOrigin(origins = "*")
public class MemoireController {

    @Autowired
    private MemoireRepository memoireRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    // GET - Récupérer tous les mémoires
    @GetMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR', 'RESPONSABLE_MASTER')")
    public ResponseEntity<List<MemoireDTO>> getAllMemoires() {
        List<Memoire> memoires = memoireRepository.findAll();
        List<MemoireDTO> dtos = memoires.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Récupérer le mémoire d'un étudiant
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR', 'RESPONSABLE_MASTER')")
    public ResponseEntity<MemoireDTO> getMemoireByStudent(@PathVariable Long studentId) {
        Memoire memoire = memoireRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Aucun mémoire trouvé pour cet étudiant"));
        return ResponseEntity.ok(convertToDTO(memoire));
    }

    // GET - Récupérer le mémoire d'un sujet
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR', 'RESPONSABLE_MASTER')")
    public ResponseEntity<MemoireDTO> getMemoireBySubject(@PathVariable Long subjectId) {
        Memoire memoire = memoireRepository.findBySubjectId(subjectId)
                .orElseThrow(() -> new RuntimeException("Aucun mémoire trouvé pour ce sujet"));
        return ResponseEntity.ok(convertToDTO(memoire));
    }

    // POST - Créer un mémoire à partir d'une candidature acceptée
    @PostMapping("/from-application/{applicationId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> createMemoireFromApplication(@PathVariable Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
        
        if (application.getStatut() != com.ujkz.memoire.GestionMemoiresBackend.enums.ApplicationStatus.ACCEPTED) {
            return ResponseEntity.badRequest().body(new MessageResponse("Cette candidature n'est pas acceptée"));
        }
        
        // Vérifier si un mémoire existe déjà pour cet étudiant ou ce sujet
        if (memoireRepository.findByStudentId(application.getStudent().getId()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Cet étudiant a déjà un mémoire"));
        }
        
        if (memoireRepository.findBySubjectId(application.getSubject().getId()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Ce sujet a déjà un mémoire associé"));
        }
        
        Memoire memoire = new Memoire();
        memoire.setApplication(application);
        memoire.setStudent(application.getStudent());
        memoire.setSubject(application.getSubject());
        memoire.setStatutAvancement(AvancementStatus.EN_COURS);
        memoire.setSoutenable(false);
        
        memoireRepository.save(memoire);
        
        return ResponseEntity.ok(new MessageResponse("Mémoire créé avec succès"));
    }

    // PATCH - Mettre à jour l'avancement du mémoire
    @PatchMapping("/{memoireId}/avancement")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> updateAvancement(@PathVariable Long memoireId, 
                                              @RequestParam AvancementStatus statut) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));
        
        memoire.setStatutAvancement(statut);
        memoireRepository.save(memoire);
        
        return ResponseEntity.ok(new MessageResponse("Statut d'avancement mis à jour avec succès"));
    }

    // PATCH - Valider la soutenabilité
    @PatchMapping("/{memoireId}/soutenabilite")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> validateSoutenabilite(@PathVariable Long memoireId,
                                                    @RequestParam Boolean soutenable,
                                                    @RequestBody(required = false) String commentaire) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));
        
        memoire.setSoutenable(soutenable);
        memoire.setDateValidationSoutenabilite(java.time.LocalDateTime.now());
        memoire.setCommentaireSoutenabilite(commentaire);
        
        if (soutenable) {
            memoire.setStatutAvancement(AvancementStatus.SOUTENABLE);
        }
        
        memoireRepository.save(memoire);
        
        return ResponseEntity.ok(new MessageResponse(
            soutenable ? "Mémoire validé comme soutenable" : "Mémoire marqué comme non soutenable"
        ));
    }

    // GET - Mémoires soutenables
    @GetMapping("/soutenables")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<MemoireDTO>> getSoutenables() {
        List<Memoire> memoires = memoireRepository.findBySoutenableTrue();
        List<MemoireDTO> dtos = memoires.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Statistiques des mémoires
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> getStats() {
        long total = memoireRepository.count();
        long soutenables = memoireRepository.countBySoutenableTrue();
        long nonSoutenus = memoireRepository.countBySoutenuFalse();
        
        return ResponseEntity.ok(new MemoireStats(total, soutenables, nonSoutenus));
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
    
    // Classe interne pour les statistiques
    static class MemoireStats {
        long total;
        long soutenables;
        long nonSoutenus;
        
        MemoireStats(long total, long soutenables, long nonSoutenus) {
            this.total = total;
            this.soutenables = soutenables;
            this.nonSoutenus = nonSoutenus;
        }
        
        public long getTotal() { return total; }
        public long getSoutenables() { return soutenables; }
        public long getNonSoutenus() { return nonSoutenus; }
    }
}