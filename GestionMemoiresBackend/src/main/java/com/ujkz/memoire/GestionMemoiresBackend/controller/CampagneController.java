package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.CampagneDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Campagne;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import com.ujkz.memoire.GestionMemoiresBackend.repository.CampagneRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/campagnes")
@CrossOrigin(origins = "*")
public class CampagneController {

    @Autowired
    private CampagneRepository campagneRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    // GET - Récupérer toutes les campagnes
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MASTER', 'ENSEIGNANT')")
    public ResponseEntity<List<CampagneDTO>> getAllCampagnes() {
        List<Campagne> campagnes = campagneRepository.findAll();
        List<CampagneDTO> dtos = campagnes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Récupérer la campagne active
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MASTER', 'ENSEIGNANT', 'ETUDIANT')")
    public ResponseEntity<CampagneDTO> getActiveCampagne() {
        Campagne campagne = campagneRepository.findByOuverteTrue()
                .orElseThrow(() -> new RuntimeException("Aucune campagne active"));
        return ResponseEntity.ok(convertToDTO(campagne));
    }

    // GET - Récupérer une campagne par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MASTER')")
    public ResponseEntity<CampagneDTO> getCampagneById(@PathVariable Long id) {
        Campagne campagne = campagneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campagne non trouvée"));
        return ResponseEntity.ok(convertToDTO(campagne));
    }

    // POST - Créer une campagne
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MASTER')")
    public ResponseEntity<?> createCampagne(@RequestBody CampagneDTO campagneDTO) {
        Semester semester = semesterRepository.findById(campagneDTO.getSemestreId())
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));

        // Vérifier s'il y a déjà une campagne ouverte
        if (campagneRepository.findByOuverteTrue().isPresent() && campagneDTO.getOuverte()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Une campagne est déjà ouverte"));
        }

        Campagne campagne = new Campagne();
        campagne.setLibelle(campagneDTO.getLibelle());
        campagne.setSemestre(semester);
        campagne.setDateDebutPropositionSujets(campagneDTO.getDateDebutPropositionSujets());
        campagne.setDateFinPropositionSujets(campagneDTO.getDateFinPropositionSujets());
        campagne.setDateDebutPositionnementEtudiants(campagneDTO.getDateDebutPositionnementEtudiants());
        campagne.setDateFinPositionnementEtudiants(campagneDTO.getDateFinPositionnementEtudiants());
        campagne.setDateLimiteDepotDocuments(campagneDTO.getDateLimiteDepotDocuments());
        campagne.setDateDebutSoutenances(campagneDTO.getDateDebutSoutenances());
        campagne.setDateFinSoutenances(campagneDTO.getDateFinSoutenances());
        campagne.setOuverte(campagneDTO.getOuverte() != null ? campagneDTO.getOuverte() : false);
        campagne.setActive(true);

        campagneRepository.save(campagne);

        return ResponseEntity.ok(new MessageResponse("Campagne créée avec succès"));
    }

    // PATCH - Ouvrir/Fermer une campagne
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MASTER')")
    public ResponseEntity<?> toggleCampagne(@PathVariable Long id) {
        Campagne campagne = campagneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campagne non trouvée"));

        // Si on veut ouvrir, vérifier qu'aucune autre n'est ouverte
        if (!campagne.getOuverte()) {
            if (campagneRepository.findByOuverteTrue().isPresent()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Une campagne est déjà ouverte"));
            }
        }

        campagne.setOuverte(!campagne.getOuverte());
        campagneRepository.save(campagne);

        String message = campagne.getOuverte() ? "Campagne ouverte avec succès" : "Campagne fermée avec succès";
        return ResponseEntity.ok(new MessageResponse(message));
    }

    // PATCH - Mettre à jour une campagne
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'RESPONSABLE_MASTER')")
    public ResponseEntity<?> updateCampagne(@PathVariable Long id,
                                            @RequestBody CampagneDTO campagneDTO) {
        Campagne campagne = campagneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campagne non trouvée"));

        if (campagneDTO.getLibelle() != null) {
            campagne.setLibelle(campagneDTO.getLibelle());
        }
        if (campagneDTO.getDateDebutPropositionSujets() != null) {
            campagne.setDateDebutPropositionSujets(campagneDTO.getDateDebutPropositionSujets());
        }
        if (campagneDTO.getDateFinPropositionSujets() != null) {
            campagne.setDateFinPropositionSujets(campagneDTO.getDateFinPropositionSujets());
        }
        if (campagneDTO.getDateDebutPositionnementEtudiants() != null) {
            campagne.setDateDebutPositionnementEtudiants(campagneDTO.getDateDebutPositionnementEtudiants());
        }
        if (campagneDTO.getDateFinPositionnementEtudiants() != null) {
            campagne.setDateFinPositionnementEtudiants(campagneDTO.getDateFinPositionnementEtudiants());
        }
        if (campagneDTO.getDateLimiteDepotDocuments() != null) {
            campagne.setDateLimiteDepotDocuments(campagneDTO.getDateLimiteDepotDocuments());
        }
        if (campagneDTO.getDateDebutSoutenances() != null) {
            campagne.setDateDebutSoutenances(campagneDTO.getDateDebutSoutenances());
        }
        if (campagneDTO.getDateFinSoutenances() != null) {
            campagne.setDateFinSoutenances(campagneDTO.getDateFinSoutenances());
        }

        campagneRepository.save(campagne);

        return ResponseEntity.ok(new MessageResponse("Campagne mise à jour avec succès"));
    }

    private CampagneDTO convertToDTO(Campagne campagne) {
        CampagneDTO dto = new CampagneDTO();
        dto.setId(campagne.getId());
        dto.setLibelle(campagne.getLibelle());
        dto.setSemestreId(campagne.getSemestre().getId());
        dto.setSemestreLibelle(campagne.getSemestre().getLibelle());
        dto.setDateDebutPropositionSujets(campagne.getDateDebutPropositionSujets());
        dto.setDateFinPropositionSujets(campagne.getDateFinPropositionSujets());
        dto.setDateDebutPositionnementEtudiants(campagne.getDateDebutPositionnementEtudiants());
        dto.setDateFinPositionnementEtudiants(campagne.getDateFinPositionnementEtudiants());
        dto.setDateLimiteDepotDocuments(campagne.getDateLimiteDepotDocuments());
        dto.setDateDebutSoutenances(campagne.getDateDebutSoutenances());
        dto.setDateFinSoutenances(campagne.getDateFinSoutenances());
        dto.setOuverte(campagne.getOuverte());
        dto.setActive(campagne.getActive());

        // Calculer le statut de la campagne
        LocalDateTime now = LocalDateTime.now();
        if (campagne.getOuverte()) {
            if (now.isBefore(campagne.getDateDebutPropositionSujets())) {
                dto.setStatut("À venir");
            } else if (now.isAfter(campagne.getDateFinSoutenances())) {
                dto.setStatut("Terminée");
            } else {
                dto.setStatut("En cours");
            }
        } else {
            dto.setStatut("Fermée");
        }

        return dto;
    }
}