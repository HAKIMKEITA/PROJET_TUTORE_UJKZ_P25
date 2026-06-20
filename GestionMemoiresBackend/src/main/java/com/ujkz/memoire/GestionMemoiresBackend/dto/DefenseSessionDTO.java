package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DefenseSessionDTO {
    private Long id;
    private String libelle;
    private LocalDateTime dateSession;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;
    private String salle;
    private Long semestreId;        // Pour la création
    private String semestreLibelle; // Pour l'affichage
    private String responsableNom;
    private Boolean active;
    private String description;
    private Integer nombreMaxSoutenances;
    private Integer nombreSoutenancesProgrammees;
    private List<DefenseDTO> defenses;
}