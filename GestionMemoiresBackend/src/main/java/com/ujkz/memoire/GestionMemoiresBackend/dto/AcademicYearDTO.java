package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AcademicYearDTO {
    private Long id;
    private String libelle;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean actif;
}