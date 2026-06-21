package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.SemesterDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.AcademicYear;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.AcademicYearRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SemesterRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Override
    public List<SemesterDTO> getAllSemesters() {
        return semesterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SemesterDTO> getSemestersByAcademicYear(Long academicYearId) {
        return semesterRepository.findByAcademicYearId(academicYearId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SemesterDTO> getActiveSemesters() {
        return semesterRepository.findByActifTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SemesterDTO getSemesterById(Long id) {
        Semester semester = getSemesterEntity(id);
        return convertToDTO(semester);
    }

    @Override
    public SemesterDTO createSemester(SemesterDTO semesterDTO) {
        AcademicYear year = academicYearRepository.findById(semesterDTO.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("Année académique non trouvée"));

        Semester semester = new Semester();
        semester.setLibelle(semesterDTO.getLibelle());
        semester.setAcademicYear(year);
        semester.setActif(semesterDTO.getActif() != null ? semesterDTO.getActif() : true);

        Semester saved = semesterRepository.save(semester);
        return convertToDTO(saved);
    }

    @Override
    public SemesterDTO updateSemester(Long id, SemesterDTO semesterDTO) {
        Semester semester = getSemesterEntity(id);

        if (semesterDTO.getLibelle() != null) semester.setLibelle(semesterDTO.getLibelle());
        if (semesterDTO.getAcademicYearId() != null) {
            AcademicYear year = academicYearRepository.findById(semesterDTO.getAcademicYearId())
                    .orElseThrow(() -> new ResourceNotFoundException("Année académique non trouvée"));
            semester.setAcademicYear(year);
        }
        if (semesterDTO.getActif() != null) semester.setActif(semesterDTO.getActif());

        Semester saved = semesterRepository.save(semester);
        return convertToDTO(saved);
    }

    @Override
    public void deleteSemester(Long id) {
        Semester semester = getSemesterEntity(id);
        semesterRepository.delete(semester);
    }

    @Override
    public Semester getSemesterEntity(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semestre non trouvé avec l'ID: " + id));
    }

    private SemesterDTO convertToDTO(Semester semester) {
        SemesterDTO dto = new SemesterDTO();
        dto.setId(semester.getId());
        dto.setLibelle(semester.getLibelle());
        dto.setAcademicYearId(semester.getAcademicYear().getId());
        dto.setAcademicYearLibelle(semester.getAcademicYear().getLibelle());
        dto.setActif(semester.isActif());
        return dto;
    }
}