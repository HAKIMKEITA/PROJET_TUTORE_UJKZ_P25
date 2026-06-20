package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseSession;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DefenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DefenseRepository extends JpaRepository<Defense, Long> {
    Optional<Defense> findByMemoire(Memoire memoire);
    List<Defense> findBySession(DefenseSession session);
    List<Defense> findByStatut(DefenseStatus statut);
    List<Defense> findBySessionAndStatut(DefenseSession session, DefenseStatus statut);
    List<Defense> findByValideeFalse();
}