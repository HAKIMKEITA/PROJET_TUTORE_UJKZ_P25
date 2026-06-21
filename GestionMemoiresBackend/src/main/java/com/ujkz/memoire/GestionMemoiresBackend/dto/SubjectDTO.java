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
    private String encadrantNom;
    private String superviseurNom;
    private String semestreLibelle;
    private int capaciteMax;
    private String statut;
    private boolean publie;
    private LocalDateTime dateCreation;
    public Long getSemestreId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSemestreId'");
    }
    public Long getEncadrantId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEncadrantId'");
    }
    public Long getSuperviseurId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSuperviseurId'");
    }
    public boolean getPublie() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPublie'");
    }
}