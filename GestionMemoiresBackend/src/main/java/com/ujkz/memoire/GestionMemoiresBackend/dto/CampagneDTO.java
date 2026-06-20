package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CampagneDTO {
    private Long id;
    private String libelle;
    private Long semestreId;
    private String semestreLibelle;
    private LocalDateTime dateDebutPropositionSujets;
    private LocalDateTime dateFinPropositionSujets;
    private LocalDateTime dateDebutPositionnementEtudiants;
    private LocalDateTime dateFinPositionnementEtudiants;
    private LocalDateTime dateLimiteDepotDocuments;
    private LocalDateTime dateDebutSoutenances;
    private LocalDateTime dateFinSoutenances;
    private Boolean ouverte;
    private Boolean active;
    private String statut; // "À venir", "En cours", "Fermée", "Terminée"
}