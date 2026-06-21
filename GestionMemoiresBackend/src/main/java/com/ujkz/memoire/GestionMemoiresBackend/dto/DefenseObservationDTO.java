package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DefenseObservationDTO {
    private Long id;
    private Long defenseId;
    private Long auteurId;
    private String auteurNom;
    private String contenu;
    private String categorie;
    private LocalDateTime dateObservation;
}