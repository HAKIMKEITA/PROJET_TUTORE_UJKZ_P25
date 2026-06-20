package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "defenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Defense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "memoire_id", nullable = false, unique = true)
    private Memoire memoire;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private DefenseSession session;
    
    private LocalDateTime dateHeure;
    
    @Enumerated(EnumType.STRING)
    private DefenseStatus statut = DefenseStatus.PROGRAMMEE;
    
    private Boolean validee = false;
    
    @Column(columnDefinition = "TEXT")
    private String rapport;
    
    private Integer dureeMinutes = 30;
    
    private String lienVisio;
}