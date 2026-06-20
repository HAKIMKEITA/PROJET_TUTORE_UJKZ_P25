package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.JuryDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.JuryMemberDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Defense;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Jury;
import com.ujkz.memoire.GestionMemoiresBackend.entity.JuryMember;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Teacher;
import com.ujkz.memoire.GestionMemoiresBackend.enums.JuryRole;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DefenseRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.JuryMemberRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.JuryRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/juries")
@CrossOrigin(origins = "*")
public class JuryController {

    @Autowired
    private JuryRepository juryRepository;

    @Autowired
    private JuryMemberRepository juryMemberRepository;

    @Autowired
    private DefenseRepository defenseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // GET - Récupérer le jury d'une soutenance
    @GetMapping("/defense/{defenseId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ENSEIGNANT', 'ADMINISTRATEUR', 'ETUDIANT')")
    public ResponseEntity<JuryDTO> getJuryByDefense(@PathVariable Long defenseId) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new RuntimeException("Soutenance non trouvée"));

        Jury jury = juryRepository.findByDefense(defense)
                .orElseThrow(() -> new RuntimeException("Aucun jury trouvé pour cette soutenance"));

        return ResponseEntity.ok(convertToDTO(jury));
    }

    // POST - Créer un jury
    @PostMapping("/defense/{defenseId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> createJury(@PathVariable Long defenseId) {
        Defense defense = defenseRepository.findById(defenseId)
                .orElseThrow(() -> new RuntimeException("Soutenance non trouvée"));

        // Vérifier si un jury existe déjà
        if (juryRepository.findByDefense(defense).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Un jury existe déjà pour cette soutenance"));
        }

        Jury jury = new Jury();
        jury.setDefense(defense);
        jury.setNombreMembresMinimal(3);
        jury.setConstitue(false);
        jury.setComplet(false);
        jury.setMembres(new ArrayList<>());

        juryRepository.save(jury);

        return ResponseEntity.ok(new MessageResponse("Jury créé avec succès"));
    }

    // POST - Ajouter un membre au jury
    @PostMapping("/{juryId}/members")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> addJuryMember(@PathVariable Long juryId,
                                           @RequestBody JuryMemberDTO memberDTO) {
        Jury jury = juryRepository.findById(juryId)
                .orElseThrow(() -> new RuntimeException("Jury non trouvé"));

        Teacher teacher = teacherRepository.findById(memberDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

        // Vérifier si l'enseignant est déjà dans le jury
        if (juryMemberRepository.existsByJuryAndTeacher(jury, teacher)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Cet enseignant est déjà dans le jury"));
        }

        // Vérifier qu'il n'y a pas déjà un président
        if (memberDTO.getRole() == JuryRole.PRESIDENT) {
            List<JuryMember> existingPresident = juryMemberRepository.findByJuryAndRole(jury, JuryRole.PRESIDENT);
            if (!existingPresident.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Un président a déjà été désigné"));
            }
        }

        // Vérifier qu'il n'y a pas déjà un encadrant
        if (memberDTO.getRole() == JuryRole.ENCADRANT) {
            List<JuryMember> existingEncadrant = juryMemberRepository.findByJuryAndRole(jury, JuryRole.ENCADRANT);
            if (!existingEncadrant.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Un encadrant a déjà été désigné"));
            }
        }

        JuryMember member = new JuryMember();
        member.setJury(jury);
        member.setTeacher(teacher);
        member.setRole(memberDTO.getRole());
        member.setPresent(false);

        juryMemberRepository.save(member);

        // Mettre à jour le statut du jury
        updateJuryStatus(jury);

        return ResponseEntity.ok(new MessageResponse("Membre ajouté au jury avec succès"));
    }

    // DELETE - Supprimer un membre du jury
    @DeleteMapping("/members/{memberId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> removeJuryMember(@PathVariable Long memberId) {
        JuryMember member = juryMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Membre du jury non trouvé"));

        Jury jury = member.getJury();
        juryMemberRepository.delete(member);

        // Mettre à jour le statut du jury
        updateJuryStatus(jury);

        return ResponseEntity.ok(new MessageResponse("Membre retiré du jury avec succès"));
    }

    // PATCH - Marquer la présence d'un membre
    @PatchMapping("/members/{memberId}/presence")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> markMemberPresence(@PathVariable Long memberId,
                                                @RequestParam Boolean present) {
        JuryMember member = juryMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Membre du jury non trouvé"));

        member.setPresent(present);
        juryMemberRepository.save(member);

        return ResponseEntity.ok(new MessageResponse(
            present ? "Présence marquée" : "Absence marquée"
        ));
    }

    // PATCH - Valider la composition du jury
    @PatchMapping("/{juryId}/validate")
    @PreAuthorize("hasAnyRole('RESPONSABLE_MASTER', 'ADMINISTRATEUR')")
    public ResponseEntity<?> validateJury(@PathVariable Long juryId) {
        Jury jury = juryRepository.findById(juryId)
                .orElseThrow(() -> new RuntimeException("Jury non trouvé"));

        List<JuryMember> members = juryMemberRepository.findByJury(jury);

        // Vérifier que le jury a au moins 3 membres
        if (members.size() < 3) {
            return ResponseEntity.badRequest().body(new MessageResponse(
                "Le jury doit avoir au moins 3 membres (actuellement: " + members.size() + ")"
            ));
        }

        // Vérifier qu'il y a un président
        List<JuryMember> presidents = juryMemberRepository.findByJuryAndRole(jury, JuryRole.PRESIDENT);
        if (presidents.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Le jury doit avoir un président"));
        }

        // Vérifier qu'il y a un encadrant
        List<JuryMember> encadrants = juryMemberRepository.findByJuryAndRole(jury, JuryRole.ENCADRANT);
        if (encadrants.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Le jury doit avoir un encadrant"));
        }

        jury.setConstitue(true);
        jury.setComplet(true);
        juryRepository.save(jury);

        return ResponseEntity.ok(new MessageResponse("Jury validé avec succès"));
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