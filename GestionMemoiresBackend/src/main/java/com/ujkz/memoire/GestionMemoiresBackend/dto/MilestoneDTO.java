package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MilestoneDTO {
    private Long id;
    private Long memoireId;
    private String libelle;
    private String description;
    private LocalDateTime echeance;
    private Boolean realise;
    private LocalDateTime dateRealisation;
    private String commentaire;
    private Integer ordre;
    private String validePar;
}