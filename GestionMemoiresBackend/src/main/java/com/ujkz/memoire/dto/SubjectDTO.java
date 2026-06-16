package com.ujkz.memoire.dto;

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
    private String encadrantNom;
    private String superviseurNom;
    private String semestreLibelle;
    private int capaciteMax;
    private String statut;
    private boolean publie;
    private LocalDateTime dateCreation;
}