package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.enums.Mention;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades_memoire")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeMemoire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "defense_id", nullable = false, unique = true)
    private Defense defense;
    
    @Column(nullable = false)
    private Integer noteFinale;
    
    @Enumerated(EnumType.STRING)
    private Mention mention;
    
    @Column(columnDefinition = "TEXT")
    private String commentaires;
    
    private Integer qualiteDocument;
    
    private Integer travailRealise;
    
    private Integer presentationOrale;
    
    private Integer reponsesQuestions;
    
    private Boolean validee = false;
    
    private LocalDateTime dateValidation;
    
    @ManyToOne
    @JoinColumn(name = "valide_par")
    private User validePar;
}