package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.StudentDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;

import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudents();
    StudentDTO getStudentById(Long id);
    StudentDTO getStudentByUserId(Long userId);
    StudentDTO getStudentByMatricule(String matricule);
    List<StudentDTO> getStudentsByPromotion(String promotion);
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    StudentDTO toggleStudentStatus(Long id);
    Student getStudentEntity(Long id);
    boolean existsByMatricule(String matricule);
}