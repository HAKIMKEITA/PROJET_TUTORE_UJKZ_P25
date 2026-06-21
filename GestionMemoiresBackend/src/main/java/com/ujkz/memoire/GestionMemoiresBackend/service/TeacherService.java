package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.TeacherDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Teacher;
import com.ujkz.memoire.GestionMemoiresBackend.enums.Grade;

import java.util.List;

public interface TeacherService {
    List<TeacherDTO> getAllTeachers();
    TeacherDTO getTeacherById(Long id);
    TeacherDTO getTeacherByUserId(Long userId);
    List<TeacherDTO> getTeachersByGrade(Grade grade);
    List<TeacherDTO> getTeachersBySpecialite(String specialite);
    TeacherDTO createTeacher(TeacherDTO teacherDTO);
    TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO);
    void deleteTeacher(Long id);
    TeacherDTO toggleTeacherStatus(Long id);
    Teacher getTeacherEntity(Long id);
}