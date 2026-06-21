package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.JuryDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.JuryMemberDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Jury;
import com.ujkz.memoire.GestionMemoiresBackend.entity.JuryMember;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Teacher;
import com.ujkz.memoire.GestionMemoiresBackend.enums.JuryRole;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.JuryMemberRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.JuryRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.TeacherRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.JuryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JuryServiceImpl implements JuryService {

    @Autowired
    private JuryRepository juryRepository;

    @Autowired
    private JuryMemberRepository juryMemberRepository;

    @Autowired
    private DefenseRepository defenseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public JuryDTO getJuryByDefense(Long defenseId) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée"));

        Jury jury = juryRepository.findByDefense(defense)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun jury trouvé pour cette soutenance"));

        return convertToDTO(jury);
    }

    @Override
    public JuryDTO createJury(Long defenseId) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée"));

        if (juryRepository.findByDefense(defense).isPresent()) {
            throw new BusinessException("Un jury existe déjà pour cette soutenance");
        }

        Jury jury = new Jury();
        jury.setDefense(defense);
        jury.setNombreMembresMinimal(3);
        jury.setConstitue(false);
        jury.setComplet(false);

        Jury saved = juryRepository.save(jury);
        return convertToDTO(saved);
    }

    @Override
    public JuryDTO addJuryMember(Long juryId, JuryMemberDTO memberDTO) {
        Jury jury = getJuryEntity(juryId);

        Teacher teacher = teacherRepository.findById(memberDTO.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé"));

        if (juryMemberRepository.existsByJuryAndTeacher(jury, teacher)) {
            throw new BusinessException("Cet enseignant est déjà dans le jury");
        }

        if (memberDTO.getRole() == JuryRole.PRESIDENT) {
            List<JuryMember> existingPresident = juryMemberRepository.findByJuryAndRole(jury, JuryRole.PRESIDENT);
            if (!existingPresident.isEmpty()) {
                throw new BusinessException("Un président a déjà été désigné");
            }
        }

        if (memberDTO.getRole() == JuryRole.ENCADRANT) {
            List<JuryMember> existingEncadrant = juryMemberRepository.findByJuryAndRole(jury, JuryRole.ENCADRANT);
            if (!existingEncadrant.isEmpty()) {
                throw new BusinessException("Un encadrant a déjà été désigné");
            }
        }

        JuryMember member = new JuryMember();
        member.setJury(jury);
        member.setTeacher(teacher);
        member.setRole(memberDTO.getRole());
        member.setPresent(false);

        juryMemberRepository.save(member);
        updateJuryStatus(jury);

        return convertToDTO(jury);
    }

    @Override
    public void removeJuryMember(Long memberId) {
        JuryMember member = juryMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre du jury non trouvé"));

        Jury jury = member.getJury();
        juryMemberRepository.delete(member);
        updateJuryStatus(jury);
    }

    @Override
    public JuryDTO markMemberPresence(Long memberId, Boolean present) {
        JuryMember member = juryMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre du jury non trouvé"));

        member.setPresent(present);
        juryMemberRepository.save(member);

        return convertToDTO(member.getJury());
    }

    @Override
    public JuryDTO validateJury(Long juryId) {
        Jury jury = getJuryEntity(juryId);
        List<JuryMember> members = juryMemberRepository.findByJury(jury);

        if (members.size() < 3) {
            throw new BusinessException("Le jury doit avoir au moins 3 membres (actuellement: " + members.size() + ")");
        }

        List<JuryMember> presidents = juryMemberRepository.findByJuryAndRole(jury, JuryRole.PRESIDENT);
        if (presidents.isEmpty()) {
            throw new BusinessException("Le jury doit avoir un président");
        }

        List<JuryMember> encadrants = juryMemberRepository.findByJuryAndRole(jury, JuryRole.ENCADRANT);
        if (encadrants.isEmpty()) {
            throw new BusinessException("Le jury doit avoir un encadrant");
        }

        jury.setConstitue(true);
        jury.setComplet(true);
        Jury saved = juryRepository.save(jury);

        return convertToDTO(saved);
    }

    @Override
    public Jury getJuryEntity(Long id) {
        return juryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jury non trouvé avec l'ID: " + id));
    }

    private void updateJuryStatus(Jury jury) {
        List<JuryMember> members = juryMemberRepository.findByJury(jury);
        jury.setComplet(members.size() >= 3);
        jury.setConstitue(members.size() >= 3);
        juryRepository.save(jury);
    }

    private JuryDTO convertToDTO(Jury jury) {
        JuryDTO dto = new JuryDTO();
        dto.setId(jury.getId());
        dto.setDefenseId(jury.getDefense().getId());
        dto.setNombreMembresMinimal(jury.getNombreMembresMinimal());
        dto.setConstitue(jury.getConstitue());
        dto.setComplet(jury.getComplet());

        List<JuryMember> members = juryMemberRepository.findByJury(jury);
        List<JuryMemberDTO> memberDTOs = members.stream()
                .map(this::convertMemberToDTO)
                .collect(Collectors.toList());
        dto.setMembres(memberDTOs);

        return dto;
    }

    private JuryMemberDTO convertMemberToDTO(JuryMember member) {
        JuryMemberDTO dto = new JuryMemberDTO();
        dto.setId(member.getId());
        dto.setJuryId(member.getJury().getId());
        dto.setTeacherId(member.getTeacher().getId());
        dto.setTeacherNom(member.getTeacher().getUser().getPrenom() + " " +
                         member.getTeacher().getUser().getNom());
        dto.setTeacherGrade(member.getTeacher().getGrade().name());
        dto.setTeacherSpecialite(member.getTeacher().getSpecialite());
        dto.setRole(member.getRole());
        dto.setRoleLibelle(member.getRole().getLibelle());
        dto.setPresent(member.getPresent());
        dto.setRemarques(member.getRemarques());
        return dto;
    }
}