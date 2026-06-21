package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubjectDTO {
    private Long id;
    private String titre;
    private String resume;
    private String objectifs;
    private String competencesRequises;
    private String motsCles;
    
    // Champs pour les IDs (nécessaires pour la création/mise à jour)
    private Long encadrantId;
    private Long superviseurId;
    private Long semestreId;
    
    // Champs pour l'affichage
    private String encadrantNom;
    private String superviseurNom;
    private String semestreLibelle;
    
    private Integer capaciteMax;
    private String statut;
    private Boolean publie;
    private LocalDateTime dateCreation;
}