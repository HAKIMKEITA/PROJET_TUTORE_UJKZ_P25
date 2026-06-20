package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.enums.JuryRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "jury_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JuryMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "jury_id", nullable = false)
    private Jury jury;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JuryRole role;
    
    private Boolean present = false;
    
    @Column(columnDefinition = "TEXT")
    private String remarques;
}