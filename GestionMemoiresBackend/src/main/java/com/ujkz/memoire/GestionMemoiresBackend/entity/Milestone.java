package com.ujkz.memoire.GestionMemoiresBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "milestones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Milestone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "memoire_id", nullable = false)
    private Memoire memoire;
    
    @Column(nullable = false)
    private String libelle;
    
    private String description;
    
    private LocalDateTime echeance;
    
    private Boolean realise = false;
    
    private LocalDateTime dateRealisation;
    
    @Column(columnDefinition = "TEXT")
    private String commentaire;
    
    private Integer ordre;
    
    @ManyToOne
    @JoinColumn(name = "valide_par")
    private User validePar;
}