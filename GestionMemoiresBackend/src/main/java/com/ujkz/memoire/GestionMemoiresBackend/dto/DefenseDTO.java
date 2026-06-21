package com.ujkz.memoire.GestionMemoiresBackend.dto;

import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DefenseDTO {
    private Long id;
    private Long memoireId;
    private String etudiantNom;
    private String sujetTitre;
    private Long sessionId;
    private String sessionLibelle;
    private LocalDateTime dateHeure;
    private DefenseStatus statut;
    private String statutLibelle;
    private Boolean validee;
    private Integer dureeMinutes;
    private String lienVisio;
    private String rapport;
    private Integer noteFinale;
    private String mention;
}