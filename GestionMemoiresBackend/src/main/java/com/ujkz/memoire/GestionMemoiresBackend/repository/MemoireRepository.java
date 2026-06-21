package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.enums.AvancementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemoireRepository extends JpaRepository<Memoire, Long> {
    Optional<Memoire> findByStudentId(Long studentId);
    Optional<Memoire> findByStudent(Student student);
    Optional<Memoire> findBySubjectId(Long subjectId);
    Optional<Memoire> findBySubject(Subject subject);
    List<Memoire> findByStatutAvancement(AvancementStatus statut);
    List<Memoire> findBySoutenableTrue();
    List<Memoire> findBySoutenableFalse();
    List<Memoire> findBySoutenuFalse();
    List<Memoire> findBySoutenuTrue();
    long countBySoutenableTrue();
    long countBySoutenuFalse();
}