package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Application;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findBySubjectId(Long subjectId);
    List<Application> findByStudentId(Long studentId);
    List<Application> findBySubjectAndStatut(Subject subject, ApplicationStatus statut);
    Optional<Application> findByStudentAndStatut(Student student, ApplicationStatus statut);
    boolean existsByStudentAndStatut(Student student, ApplicationStatus statut);
    long countBySubjectAndStatut(Subject subject, ApplicationStatus statut);
}