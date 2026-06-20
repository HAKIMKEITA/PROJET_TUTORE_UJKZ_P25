package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findByMemoireOrderByOrdreAsc(Memoire memoire);
    List<Milestone> findByMemoireAndRealiseFalse(Memoire memoire);
    List<Milestone> findByMemoireAndRealiseTrue(Memoire memoire);
}