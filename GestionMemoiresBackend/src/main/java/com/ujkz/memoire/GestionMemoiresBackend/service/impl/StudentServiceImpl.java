package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.StudentDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.UserRole;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.StudentRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = getStudentEntity(id);
        return convertToDTO(student);
    }

    @Override
    public StudentDTO getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé pour l'utilisateur ID: " + userId));
        return convertToDTO(student);
    }

    @Override
    public StudentDTO getStudentByMatricule(String matricule) {
        Student student = studentRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec le matricule: " + matricule));
        return convertToDTO(student);
    }

    @Override
    public List<StudentDTO> getStudentsByPromotion(String promotion) {
        return studentRepository.findByPromotion(promotion).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (studentRepository.existsByMatricule(studentDTO.getMatricule())) {
            throw new BusinessException("Un étudiant avec ce matricule existe déjà");
        }

        if (userRepository.existsByEmail(studentDTO.getUserEmail())) {
            throw new BusinessException("Un utilisateur avec cet email existe déjà");
        }

        // Créer l'utilisateur associé
        User user = new User();
        user.setNom(studentDTO.getUserNom());
        user.setPrenom(studentDTO.getUserPrenom());
        user.setEmail(studentDTO.getUserEmail());
        user.setPassword(passwordEncoder.encode("password123")); // Mot de passe par défaut
        user.setRole(UserRole.ETUDIANT);
        user.setActif(true);
        
        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setMatricule(studentDTO.getMatricule());
        student.setPromotion(studentDTO.getPromotion());
        student.setMasterSpecialite(studentDTO.getMasterSpecialite());
        student.setSemestre(studentDTO.getSemestre());
        student.setActif(true);

        Student saved = studentRepository.save(student);
        return convertToDTO(saved);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = getStudentEntity(id);
        User user = student.getUser();

        if (studentDTO.getUserNom() != null) user.setNom(studentDTO.getUserNom());
        if (studentDTO.getUserPrenom() != null) user.setPrenom(studentDTO.getUserPrenom());
        if (studentDTO.getUserEmail() != null) user.setEmail(studentDTO.getUserEmail());
        if (studentDTO.getMatricule() != null) {
            if (studentRepository.existsByMatricule(studentDTO.getMatricule()) && 
                !student.getMatricule().equals(studentDTO.getMatricule())) {
                throw new BusinessException("Un étudiant avec ce matricule existe déjà");
            }
            student.setMatricule(studentDTO.getMatricule());
        }
        if (studentDTO.getPromotion() != null) student.setPromotion(studentDTO.getPromotion());
        if (studentDTO.getMasterSpecialite() != null) student.setMasterSpecialite(studentDTO.getMasterSpecialite());
        if (studentDTO.getSemestre() != null) student.setSemestre(studentDTO.getSemestre());

        userRepository.save(user);
        Student saved = studentRepository.save(student);
        return convertToDTO(saved);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentEntity(id);
        User user = student.getUser();
        studentRepository.delete(student);
        userRepository.delete(user);
    }

    @Override
    public StudentDTO toggleStudentStatus(Long id) {
        Student student = getStudentEntity(id);
        student.setActif(!student.isActif());
        User user = student.getUser();
        user.setActif(student.isActif());
        userRepository.save(user);
        Student saved = studentRepository.save(student);
        return convertToDTO(saved);
    }

    @Override
    public Student getStudentEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + id));
    }

    @Override
    public boolean existsByMatricule(String matricule) {
        return studentRepository.existsByMatricule(matricule);
    }

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUserId(student.getUser().getId());
        dto.setUserNom(student.getUser().getNom());
        dto.setUserPrenom(student.getUser().getPrenom());
        dto.setUserEmail(student.getUser().getEmail());
        dto.setMatricule(student.getMatricule());
        dto.setPromotion(student.getPromotion());
        dto.setMasterSpecialite(student.getMasterSpecialite());
        dto.setSemestre(student.getSemestre());
        dto.setActif(student.isActif());
        return dto;
    }
}