package com.ujkz.memoire.GestionMemoiresBackend.dto;

import com.ujkz.memoire.GestionMemoiresBackend.enums.ObservationType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ObservationDTO {
    private Long id;
    private Long memoireId;
    private Long auteurId;
    private String auteurNom;
    private String auteurRole;
    private String contenu;
    private LocalDateTime dateObservation;
    private ObservationType typeObservation;
    private String typeObservationLibelle;
    private Boolean vuParEtudiant;
    private LocalDateTime dateVu;
}