package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.TeacherDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Teacher;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.Grade;
import com.ujkz.memoire.GestionMemoiresBackend.enums.UserRole;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.TeacherRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDTO getTeacherById(Long id) {
        Teacher teacher = getTeacherEntity(id);
        return convertToDTO(teacher);
    }

    @Override
    public TeacherDTO getTeacherByUserId(Long userId) {
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé pour l'utilisateur ID: " + userId));
        return convertToDTO(teacher);
    }

    @Override
    public List<TeacherDTO> getTeachersByGrade(Grade grade) {
        return teacherRepository.findByGrade(grade).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherDTO> getTeachersBySpecialite(String specialite) {
        return teacherRepository.findBySpecialiteContainingIgnoreCase(specialite).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        // Créer l'utilisateur associé
        User user = new User();
        user.setNom(teacherDTO.getUserNom());
        user.setPrenom(teacherDTO.getUserPrenom());
        user.setEmail(teacherDTO.getUserEmail());
        user.setPassword(passwordEncoder.encode("password123")); // Mot de passe par défaut
        user.setRole(UserRole.ENSEIGNANT);
        user.setActif(true);
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Un utilisateur avec cet email existe déjà");
        }
        
        User savedUser = userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setUser(savedUser);
        teacher.setGrade(teacherDTO.getGrade());
        teacher.setSpecialite(teacherDTO.getSpecialite());
        teacher.setActif(true);

        Teacher saved = teacherRepository.save(teacher);
        return convertToDTO(saved);
    }

    @Override
    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher teacher = getTeacherEntity(id);
        User user = teacher.getUser();

        if (teacherDTO.getUserNom() != null) user.setNom(teacherDTO.getUserNom());
        if (teacherDTO.getUserPrenom() != null) user.setPrenom(teacherDTO.getUserPrenom());
        if (teacherDTO.getUserEmail() != null) user.setEmail(teacherDTO.getUserEmail());
        if (teacherDTO.getGrade() != null) teacher.setGrade(teacherDTO.getGrade());
        if (teacherDTO.getSpecialite() != null) teacher.setSpecialite(teacherDTO.getSpecialite());

        userRepository.save(user);
        Teacher saved = teacherRepository.save(teacher);
        return convertToDTO(saved);
    }

    @Override
    public void deleteTeacher(Long id) {
        Teacher teacher = getTeacherEntity(id);
        // Supprimer également l'utilisateur associé
        User user = teacher.getUser();
        teacherRepository.delete(teacher);
        userRepository.delete(user);
    }

    @Override
    public TeacherDTO toggleTeacherStatus(Long id) {
        Teacher teacher = getTeacherEntity(id);
        teacher.setActif(!teacher.isActif());
        // Désactiver également l'utilisateur
        User user = teacher.getUser();
        user.setActif(teacher.isActif());
        userRepository.save(user);
        Teacher saved = teacherRepository.save(teacher);
        return convertToDTO(saved);
    }

    @Override
    public Teacher getTeacherEntity(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant non trouvé avec l'ID: " + id));
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setUserId(teacher.getUser().getId());
        dto.setUserNom(teacher.getUser().getNom());
        dto.setUserPrenom(teacher.getUser().getPrenom());
        dto.setUserEmail(teacher.getUser().getEmail());
        dto.setGrade(teacher.getGrade());
        dto.setSpecialite(teacher.getSpecialite());
        dto.setActif(teacher.isActif());
        return dto;
    }
}