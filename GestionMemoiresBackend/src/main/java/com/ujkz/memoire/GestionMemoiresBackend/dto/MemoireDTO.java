package com.ujkz.memoire.GestionMemoiresBackend.dto;

import com.ujkz.memoire.GestionMemoiresBackend.enums.AvancementStatus;
import com.ujkz.memoire.GestionMemoiresBackend.enums.Mention;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemoireDTO {
    private Long id;
    private Long studentId;
    private String studentNom;
    private String studentPrenom;
    private String studentMatricule;
    private Long subjectId;
    private String subjectTitre;
    private String encadrantNom;
    private String superviseurNom;
    private LocalDateTime dateAffectation;
    private AvancementStatus statutAvancement;
    private String statutAvancementLibelle;
    private Boolean soutenable;
    private LocalDateTime dateValidationSoutenabilite;
    private String commentaireSoutenabilite;
    private Boolean soutenu;
    private Integer noteFinale;
    private String mention;
    private LocalDateTime dateSoutenance;
}