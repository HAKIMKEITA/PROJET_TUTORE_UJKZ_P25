package com.ujkz.memoire.repository;

import com.ujkz.memoire.entity.Subject;
import com.ujkz.memoire.enums.SubjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByEncadrantId(Long encadrantId);
    List<Subject> findByStatut(SubjectStatus statut);
    List<Subject> findByPublieTrue();
    List<Subject> findBySemestreId(Long semestreId);
}