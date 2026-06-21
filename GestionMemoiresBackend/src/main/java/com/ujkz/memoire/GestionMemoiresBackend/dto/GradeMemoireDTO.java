package com.ujkz.memoire.GestionMemoiresBackend.dto;

import com.ujkz.memoire.GestionMemoiresBackend.enums.Mention;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GradeMemoireDTO {
    private Long id;
    private Long defenseId;
    private String etudiantNom;
    private String sujetTitre;
    private Integer noteFinale;
    private String mention;
    private String commentaires;
    private Integer qualiteDocument;
    private Integer travailRealise;
    private Integer presentationOrale;
    private Integer reponsesQuestions;
    private Boolean validee;
    private LocalDateTime dateValidation;
    private String validePar;
}