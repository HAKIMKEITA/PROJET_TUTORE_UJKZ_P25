package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.enums.AvancementStatus;
import com.ujkz.memoire.GestionMemoiresBackend.enums.Mention;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "memoires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memoire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;
    
    @OneToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;
    
    @OneToOne
    @JoinColumn(name = "subject_id", nullable = false, unique = true)
    private Subject subject;
    
    private LocalDateTime dateAffectation;
    
    @Enumerated(EnumType.STRING)
    private AvancementStatus statutAvancement = AvancementStatus.EN_COURS;
    
    private Boolean soutenable = false;
    
    private LocalDateTime dateValidationSoutenabilite;
    
    @Column(columnDefinition = "TEXT")
    private String commentaireSoutenabilite;
    
    private Integer noteFinale;
    
    @Enumerated(EnumType.STRING)
    private Mention mention;
    
    private Boolean soutenu = false;
    
    private LocalDateTime dateSoutenance;
    
    @Column(columnDefinition = "TEXT")
    private String rapportFinal;
    
    @PrePersist
    protected void onCreate() {
        dateAffectation = LocalDateTime.now();
    }
}