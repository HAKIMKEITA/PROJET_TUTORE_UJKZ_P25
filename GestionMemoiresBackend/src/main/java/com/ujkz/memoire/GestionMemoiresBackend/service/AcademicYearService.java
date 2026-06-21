package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.AcademicYearDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.AcademicYear;

import java.util.List;

public interface AcademicYearService {
    List<AcademicYearDTO> getAllAcademicYears();
    AcademicYearDTO getActiveAcademicYear();
    AcademicYearDTO getAcademicYearById(Long id);
    AcademicYearDTO createAcademicYear(AcademicYearDTO academicYearDTO);
    AcademicYearDTO updateAcademicYear(Long id, AcademicYearDTO academicYearDTO);
    void deleteAcademicYear(Long id);
    AcademicYearDTO activateAcademicYear(Long id);
    AcademicYear getAcademicYearEntity(Long id);
}