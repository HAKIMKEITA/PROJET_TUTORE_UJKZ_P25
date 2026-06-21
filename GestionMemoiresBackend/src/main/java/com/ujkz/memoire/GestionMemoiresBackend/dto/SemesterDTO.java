package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;

@Data
public class SemesterDTO {
    private Long id;
    private String libelle;
    private Long academicYearId;
    private String academicYearLibelle;
    private Boolean actif;
}