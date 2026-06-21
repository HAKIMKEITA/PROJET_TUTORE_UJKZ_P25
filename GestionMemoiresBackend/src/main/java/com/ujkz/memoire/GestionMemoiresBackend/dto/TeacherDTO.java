package com.ujkz.memoire.GestionMemoiresBackend.dto;

import com.ujkz.memoire.GestionMemoiresBackend.enums.Grade;
import lombok.Data;

@Data
public class TeacherDTO {
    private Long id;
    private Long userId;
    private String userNom;
    private String userPrenom;
    private String userEmail;
    private Grade grade;
    private String specialite;
    private Boolean actif;
}