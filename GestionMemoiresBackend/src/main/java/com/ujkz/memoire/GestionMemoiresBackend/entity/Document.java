package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "memoire_id", nullable = false)
    private Memoire memoire;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;
    
    @Column(nullable = false)
    private String nomFichier;
    
    private String cheminFichier;
    
    private LocalDateTime dateDepot;
    
    private Integer version;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Boolean valideParEncadrant = false;
    
    private LocalDateTime dateValidation;
    
    @ManyToOne
    @JoinColumn(name = "valide_par")
    private User validePar;
    
    @PrePersist
    protected void onCreate() {
        dateDepot = LocalDateTime.now();
        if (version == null) version = 1;
    }
}