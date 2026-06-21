package com.ujkz.memoire.GestionMemoiresBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "campagnes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campagne {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String libelle;
    
    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semester semestre;
    
    private LocalDateTime dateDebutPropositionSujets;
    
    private LocalDateTime dateFinPropositionSujets;
    
    private LocalDateTime dateDebutPositionnementEtudiants;
    
    private LocalDateTime dateFinPositionnementEtudiants;
    
    private LocalDateTime dateLimiteDepotDocuments;
    
    private LocalDateTime dateDebutSoutenances;
    
    private LocalDateTime dateFinSoutenances;
    
    private Boolean ouverte = false;
    
    private Boolean active = true;
}