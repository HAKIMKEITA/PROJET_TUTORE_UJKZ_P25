package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseSession;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DefenseSessionRepository extends JpaRepository<DefenseSession, Long> {
    List<DefenseSession> findBySemestre(Semester semestre);
    List<DefenseSession> findByActiveTrue();
    List<DefenseSession> findByDateSessionBetween(LocalDateTime debut, LocalDateTime fin);
}