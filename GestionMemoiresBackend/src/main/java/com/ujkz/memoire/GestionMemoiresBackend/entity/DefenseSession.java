package com.ujkz.memoire.GestionMemoiresBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "defense_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefenseSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String libelle;
    
    private LocalDateTime dateSession;
    
    private LocalDateTime heureDebut;
    
    private LocalDateTime heureFin;
    
    private String salle;
    
    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semester semestre;
    
    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private User responsable;
    
    private Boolean active = true;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer nombreMaxSoutenances = 10;
}