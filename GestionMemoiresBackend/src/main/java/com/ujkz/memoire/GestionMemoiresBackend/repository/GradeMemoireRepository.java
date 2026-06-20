package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.GradeMemoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GradeMemoireRepository extends JpaRepository<GradeMemoire, Long> {
    Optional<GradeMemoire> findByDefense(Defense defense);
}