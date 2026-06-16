package com.ujkz.memoire.repository;

import com.ujkz.memoire.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByMatricule(String matricule);
    List<Student> findByPromotion(String promotion);
    boolean existsByMatricule(String matricule);
}