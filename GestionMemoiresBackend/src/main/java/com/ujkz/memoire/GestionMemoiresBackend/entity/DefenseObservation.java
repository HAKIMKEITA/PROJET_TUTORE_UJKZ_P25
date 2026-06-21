package com.ujkz.memoire.GestionMemoiresBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "defense_observations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefenseObservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "defense_id", nullable = false)
    private Defense defense;
    
    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private User auteur;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;
    
    private String categorie; // qualite_scientifique, qualite_redactionnelle, presentation_orale, etc.
    
    private LocalDateTime dateObservation;
    
    @PrePersist
    protected void onCreate() {
        dateObservation = LocalDateTime.now();
    }
}