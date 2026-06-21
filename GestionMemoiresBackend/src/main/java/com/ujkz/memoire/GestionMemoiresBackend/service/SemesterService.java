package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.SemesterDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;

import java.util.List;

public interface SemesterService {
    List<SemesterDTO> getAllSemesters();
    List<SemesterDTO> getSemestersByAcademicYear(Long academicYearId);
    List<SemesterDTO> getActiveSemesters();
    SemesterDTO getSemesterById(Long id);
    SemesterDTO createSemester(SemesterDTO semesterDTO);
    SemesterDTO updateSemester(Long id, SemesterDTO semesterDTO);
    void deleteSemester(Long id);
    Semester getSemesterEntity(Long id);
}