package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.enums.ObservationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "observations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Observation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "memoire_id", nullable = false)
    private Memoire memoire;
    
    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private User auteur;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;
    
    private LocalDateTime dateObservation;
    
    @Enumerated(EnumType.STRING)
    private ObservationType typeObservation;
    
    private Boolean vuParEtudiant = false;
    
    private LocalDateTime dateVu;
    
    @PrePersist
    protected void onCreate() {
        dateObservation = LocalDateTime.now();
    }
}