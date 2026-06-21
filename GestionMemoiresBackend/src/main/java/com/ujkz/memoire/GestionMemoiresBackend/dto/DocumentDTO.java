package com.ujkz.memoire.GestionMemoiresBackend.dto;

import com.ujkz.memoire.GestionMemoiresBackend.enums.DocumentType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private Long id;
    private Long memoireId;
    private DocumentType type;
    private String typeLibelle;
    private String nomFichier;
    private String cheminFichier;
    private LocalDateTime dateDepot;
    private Integer version;
    private String description;
    private Boolean valideParEncadrant;
    private LocalDateTime dateValidation;
    private String validePar;
}