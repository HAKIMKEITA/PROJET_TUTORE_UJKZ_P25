package com.ujkz.memoire.repository;

import com.ujkz.memoire.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByLibelle(String libelle);
    Optional<AcademicYear> findByActifTrue();
}