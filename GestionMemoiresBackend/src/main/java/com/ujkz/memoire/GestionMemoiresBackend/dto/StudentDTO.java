package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private Long userId;
    private String userNom;
    private String userPrenom;
    private String userEmail;
    private String matricule;
    private String promotion;
    private String masterSpecialite;
    private String semestre;
    private Boolean actif;
}