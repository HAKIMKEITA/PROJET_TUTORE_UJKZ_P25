package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.DefenseObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DefenseObservationRepository extends JpaRepository<DefenseObservation, Long> {
    List<DefenseObservation> findByDefenseOrderByDateObservationDesc(Defense defense);
}