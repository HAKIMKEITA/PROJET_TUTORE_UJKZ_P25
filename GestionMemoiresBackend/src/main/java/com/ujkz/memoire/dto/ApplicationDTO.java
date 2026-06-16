package com.ujkz.memoire.dto;

import com.ujkz.memoire.enums.ApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationDTO {
    private Long id;
    private Long studentId;
    private String studentNom;
    private String studentPrenom;
    private String studentMatricule;
    private Long subjectId;
    private String subjectTitre;
    private String motivation;
    private ApplicationStatus statut;
    private String commentaireEncadrant;
    private LocalDateTime dateCandidature;
}