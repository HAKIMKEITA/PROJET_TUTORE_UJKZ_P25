package com.ujkz.memoire.GestionMemoiresBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "juries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jury {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "defense_id", nullable = false, unique = true)
    private Defense defense;
    
    private Integer nombreMembresMinimal = 3;
    
    private Boolean constitue = false;
    
    private Boolean complet = false;
    
    @OneToMany(mappedBy = "jury", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JuryMember> membres = new ArrayList<>();
}