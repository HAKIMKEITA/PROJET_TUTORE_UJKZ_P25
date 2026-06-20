package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Campagne;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampagneRepository extends JpaRepository<Campagne, Long> {
    List<Campagne> findBySemestre(Semester semestre);
    Optional<Campagne> findByOuverteTrue();
    List<Campagne> findByActiveTrue();
}   