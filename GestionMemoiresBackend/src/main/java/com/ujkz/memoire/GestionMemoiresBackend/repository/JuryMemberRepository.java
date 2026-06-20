package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Jury;
import com.ujkz.memoire.GestionMemoiresBackend.entity.JuryMember;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Teacher;
import com.ujkz.memoire.GestionMemoiresBackend.enums.JuryRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JuryMemberRepository extends JpaRepository<JuryMember, Long> {
    List<JuryMember> findByJury(Jury jury);
    List<JuryMember> findByJuryAndRole(Jury jury, JuryRole role);
    boolean existsByJuryAndTeacher(Jury jury, Teacher teacher);
}