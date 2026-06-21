package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.GradeMemoireDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.GradeMemoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import com.ujkz.memoire.GestionMemoiresBackend.enums.Mention;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.GradeMemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/grades")
@CrossOrigin(origins = "*")
public class GradeController {

    @Autowired
    private GradeMemoireRepository gradeMemoireRepository;
    
    @Autowired
    private DefenseRepository defenseRepository;
    
    @Autowired
    private MemoireRepository memoireRepository;
    
    @Autowired
    private UserRepository userRepository;

    // POST - Enregistrer la note d'un mémoire
    @PostMapping("/defense/{defenseId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> saveGrade(@PathVariable Long defenseId, 
                                       @RequestBody GradeMemoireDTO gradeDTO) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new RuntimeException("Soutenance non trouvée"));
        
        // Vérifier que la soutenance est terminée
        if (defense.getStatut() != DefenseStatus.TERMINEE) {
            return ResponseEntity.badRequest().body(new MessageResponse("La soutenance n'est pas encore terminée"));
        }
        
        // Vérifier si une note existe déjà
        if (gradeMemoireRepository.findByDefense(defense).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Une note existe déjà pour cette soutenance"));
        }
        
        // Créer la note
        GradeMemoire grade = new GradeMemoire();
        grade.setDefense(defense);
        grade.setNoteFinale(gradeDTO.getNoteFinale());
        grade.setQualiteDocument(gradeDTO.getQualiteDocument());
        grade.setTravailRealise(gradeDTO.getTravailRealise());
        grade.setPresentationOrale(gradeDTO.getPresentationOrale());
        grade.setReponsesQuestions(gradeDTO.getReponsesQuestions());
        grade.setCommentaires(gradeDTO.getCommentaires());
        
        // Calculer la mention
        int note = gradeDTO.getNoteFinale();
        if (note >= 16) grade.setMention(Mention.EXCELLENT);
        else if (note >= 14) grade.setMention(Mention.TRES_BIEN);
        else if (note >= 12) grade.setMention(Mention.BIEN);
        else if (note >= 10) grade.setMention(Mention.ASSEZ_BIEN);
        else grade.setMention(Mention.PASSABLE);
        
        // Valider la note
        grade.setValidee(true);
        grade.setDateValidation(LocalDateTime.now());
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        grade.setValidePar(currentUser);
        
        gradeMemoireRepository.save(grade);
        
        // Mettre à jour le mémoire
        Memoire memoire = defense.getMemoire();
        memoire.setNoteFinale(grade.getNoteFinale());
        memoire.setMention(grade.getMention());
        memoire.setSoutenu(true);
        memoire.setDateSoutenance(defense.getDateHeure());
        memoireRepository.save(memoire);
        
        return ResponseEntity.ok(new MessageResponse("Note enregistrée avec succès"));
    }

    // GET - Récupérer la note d'une soutenance
    @GetMapping("/defense/{defenseId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ENSEIGNANT', 'ADMINISTRATEUR', 'ETUDIANT')")
    public ResponseEntity<GradeMemoireDTO> getGradeByDefense(@PathVariable Long defenseId) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new RuntimeException("Soutenance non trouvée"));
        
        GradeMemoire grade = gradeMemoireRepository.findByDefense(defense)
                .orElseThrow(() -> new RuntimeException("Aucune note trouvée pour cette soutenance"));
        
        return ResponseEntity.ok(convertToDTO(grade));
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