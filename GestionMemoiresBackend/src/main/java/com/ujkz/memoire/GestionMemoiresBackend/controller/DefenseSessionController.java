package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.DefenseSessionDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseSession;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseSessionRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/defense-sessions")
@CrossOrigin(origins = "*")
public class DefenseSessionController {

    @Autowired
    private DefenseSessionRepository defenseSessionRepository;
    
    @Autowired
    private DefenseRepository defenseRepository;
    
    @Autowired
    private MemoireRepository memoireRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;

    // GET - Toutes les sessions
    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<DefenseSessionDTO>> getAllSessions() {
        List<DefenseSession> sessions = defenseSessionRepository.findAll();
        List<DefenseSessionDTO> dtos = sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST - Créer une session
    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> createSession(@RequestBody DefenseSessionDTO sessionDTO) {
        // CORRECTION: Utiliser getSemestreId() au lieu de getSemestreLibelle()
        Semester semester = semesterRepository.findById(sessionDTO.getSemestreId())
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé avec l'ID: " + sessionDTO.getSemestreId()));
        
        DefenseSession session = new DefenseSession();
        session.setLibelle(sessionDTO.getLibelle());
        session.setDateSession(sessionDTO.getDateSession());
        session.setHeureDebut(sessionDTO.getHeureDebut());
        session.setHeureFin(sessionDTO.getHeureFin());
        session.setSalle(sessionDTO.getSalle());
        session.setSemestre(semester);
        session.setDescription(sessionDTO.getDescription());
        session.setNombreMaxSoutenances(sessionDTO.getNombreMaxSoutenances());
        session.setActive(true);
        
        defenseSessionRepository.save(session);
        
        return ResponseEntity.ok(new MessageResponse("Session de soutenance créée avec succès"));
    }

    // GET - Session par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<DefenseSessionDTO> getSessionById(@PathVariable Long id) {
        DefenseSession session = defenseSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session non trouvée avec l'ID: " + id));
        return ResponseEntity.ok(convertToDTO(session));
    }

    // POST - Ajouter un mémoire à une session
    @PostMapping("/{sessionId}/add-memoire/{memoireId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> addMemoireToSession(@PathVariable Long sessionId, 
                                                  @PathVariable Long memoireId) {
        DefenseSession session = defenseSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée avec l'ID: " + sessionId));
        
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé avec l'ID: " + memoireId));
        
        // Vérifier si le mémoire est soutenable
        if (!memoire.getSoutenable()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Ce mémoire n'est pas soutenable"));
        }
        
        // Vérifier si le mémoire est déjà programmé
        if (defenseRepository.findByMemoire(memoire).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Ce mémoire est déjà programmé"));
        }
        
        Defense defense = new Defense();
        defense.setMemoire(memoire);
        defense.setSession(session);
        defense.setDateHeure(session.getDateSession());
        defense.setStatut(DefenseStatus.PROGRAMMEE);
        defense.setValidee(false);
        
        defenseRepository.save(defense);
        
        // Mettre à jour le mémoire
        memoire.setSoutenu(false);
        memoireRepository.save(memoire);
        
        return ResponseEntity.ok(new MessageResponse("Mémoire programmé pour la soutenance"));
    }

    // PATCH - Valider une session
    @PatchMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> validateSession(@PathVariable Long id) {
        DefenseSession session = defenseSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session non trouvée avec l'ID: " + id));
        
        session.setActive(false);
        defenseSessionRepository.save(session);
        
        return ResponseEntity.ok(new MessageResponse("Session validée avec succès"));
    }

    private DefenseSessionDTO convertToDTO(DefenseSession session) {
        DefenseSessionDTO dto = new DefenseSessionDTO();
        dto.setId(session.getId());
        dto.setLibelle(session.getLibelle());
        dto.setDateSession(session.getDateSession());
        dto.setHeureDebut(session.getHeureDebut());
        dto.setHeureFin(session.getHeureFin());
        dto.setSalle(session.getSalle());
        dto.setSemestreId(session.getSemestre().getId());  // Ajouté
        dto.setSemestreLibelle(session.getSemestre().getLibelle());
        dto.setActive(session.getActive());
        dto.setDescription(session.getDescription());
        dto.setNombreMaxSoutenances(session.getNombreMaxSoutenances());
        
        List<Defense> defenses = defenseRepository.findBySession(session);
        dto.setNombreSoutenancesProgrammees(defenses.size());
        
        List<DefenseDTO> defenseDTOs = defenses.stream()
                .map(this::convertDefenseToDTO)
                .collect(Collectors.toList());
        dto.setDefenses(defenseDTOs);
        
        return dto;
    }
    
    private DefenseDTO convertDefenseToDTO(Defense defense) {
        DefenseDTO dto = new DefenseDTO();
        dto.setId(defense.getId());
        dto.setMemoireId(defense.getMemoire().getId());
        dto.setEtudiantNom(defense.getMemoire().getStudent().getUser().getPrenom() + " " + 
                          defense.getMemoire().getStudent().getUser().getNom());
        dto.setSujetTitre(defense.getMemoire().getSubject().getTitre());
        dto.setSessionId(defense.getSession().getId());
        dto.setSessionLibelle(defense.getSession().getLibelle());
        dto.setDateHeure(defense.getDateHeure());
        dto.setStatut(defense.getStatut());
        dto.setStatutLibelle(defense.getStatut().getLibelle());
        dto.setValidee(defense.getValidee());
        dto.setDureeMinutes(defense.getDureeMinutes());
        dto.setLienVisio(defense.getLienVisio());
        
        if (defense.getMemoire().getNoteFinale() != null) {
            dto.setNoteFinale(defense.getMemoire().getNoteFinale());
        }
        if (defense.getMemoire().getMention() != null) {
            dto.setMention(defense.getMemoire().getMention().getLibelle());
        }
        
        return dto;
    }
}