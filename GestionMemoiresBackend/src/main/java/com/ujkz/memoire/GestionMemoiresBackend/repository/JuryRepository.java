package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Jury;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JuryRepository extends JpaRepository<Jury, Long> {
    Optional<Jury> findByDefense(Defense defense);
}