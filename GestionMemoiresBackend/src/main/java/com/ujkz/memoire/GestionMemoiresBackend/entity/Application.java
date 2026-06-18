package com.ujkz.memoire.GestionMemoiresBackend.entity;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @Column(columnDefinition = "TEXT")
    private String motivation;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus statut = ApplicationStatus.PENDING;
    
    private LocalDateTime dateCandidature;
    
    @Column(columnDefinition = "TEXT")
    private String commentaireEncadrant;
    
    @PrePersist
    protected void onCreate() {
        dateCandidature = LocalDateTime.now();
    }
}   