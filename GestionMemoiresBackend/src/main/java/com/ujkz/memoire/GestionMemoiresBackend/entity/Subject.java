package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.enums.SubjectStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String resume;
    
    @Column(columnDefinition = "TEXT")
    private String objectifs;
    
    private String competencesRequises;
    
    private String motsCles;
    
    @ManyToOne
    @JoinColumn(name = "encadrant_id", nullable = false)
    private Teacher encadrant;
    
    @ManyToOne
    @JoinColumn(name = "superviseur_id", nullable = false)
    private Teacher superviseur;
    
    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semester semestre;
    
    private int capaciteMax = 1;
    
    @Enumerated(EnumType.STRING)
    private SubjectStatus statut = SubjectStatus.OUVERT;
    
    private boolean publie = false;
    
    private LocalDateTime dateCreation;
    
    private LocalDateTime datePublication;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}