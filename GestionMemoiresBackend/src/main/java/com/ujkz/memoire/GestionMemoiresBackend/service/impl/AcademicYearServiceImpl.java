package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.AcademicYearDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.AcademicYear;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.AcademicYearRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AcademicYearServiceImpl implements AcademicYearService {

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Override
    public List<AcademicYearDTO> getAllAcademicYears() {
        return academicYearRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AcademicYearDTO getActiveAcademicYear() {
        AcademicYear year = academicYearRepository.findByActifTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Aucune année académique active"));
        return convertToDTO(year);
    }

    @Override
    public AcademicYearDTO getAcademicYearById(Long id) {
        AcademicYear year = getAcademicYearEntity(id);
        return convertToDTO(year);
    }

    @Override
    public AcademicYearDTO createAcademicYear(AcademicYearDTO academicYearDTO) {
        if (academicYearRepository.findByLibelle(academicYearDTO.getLibelle()).isPresent()) {
            throw new BusinessException("Une année académique avec ce libellé existe déjà");
        }

        AcademicYear year = new AcademicYear();
        year.setLibelle(academicYearDTO.getLibelle());
        year.setDateDebut(academicYearDTO.getDateDebut());
        year.setDateFin(academicYearDTO.getDateFin());
        year.setActif(academicYearDTO.getActif() != null ? academicYearDTO.getActif() : false);

        if (academicYearRepository.count() == 0) {
            year.setActif(true);
        }

        if (year.isActif()) {
            academicYearRepository.findAll().forEach(y -> {
                y.setActif(false);
                academicYearRepository.save(y);
            });
        }

        AcademicYear saved = academicYearRepository.save(year);
        return convertToDTO(saved);
    }

    @Override
    public AcademicYearDTO updateAcademicYear(Long id, AcademicYearDTO academicYearDTO) {
        AcademicYear year = getAcademicYearEntity(id);

        if (academicYearDTO.getLibelle() != null) {
            if (academicYearRepository.findByLibelle(academicYearDTO.getLibelle()).isPresent() &&
                !academicYearDTO.getLibelle().equals(year.getLibelle())) {
                throw new BusinessException("Une année académique avec ce libellé existe déjà");
            }
            year.setLibelle(academicYearDTO.getLibelle());
        }
        if (academicYearDTO.getDateDebut() != null) year.setDateDebut(academicYearDTO.getDateDebut());
        if (academicYearDTO.getDateFin() != null) year.setDateFin(academicYearDTO.getDateFin());

        AcademicYear saved = academicYearRepository.save(year);
        return convertToDTO(saved);
    }

    @Override
    public void deleteAcademicYear(Long id) {
        AcademicYear year = getAcademicYearEntity(id);
        academicYearRepository.delete(year);
    }

    @Override
    public AcademicYearDTO activateAcademicYear(Long id) {
        academicYearRepository.findAll().forEach(y -> {
            y.setActif(false);
            academicYearRepository.save(y);
        });

        AcademicYear year = getAcademicYearEntity(id);
        year.setActif(true);
        AcademicYear saved = academicYearRepository.save(year);
        return convertToDTO(saved);
    }

    @Override
    public AcademicYear getAcademicYearEntity(Long id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Année académique non trouvée avec l'ID: " + id));
    }

    private AcademicYearDTO convertToDTO(AcademicYear year) {
        AcademicYearDTO dto = new AcademicYearDTO();
        dto.setId(year.getId());
        dto.setLibelle(year.getLibelle());
        dto.setDateDebut(year.getDateDebut());
        dto.setDateFin(year.getDateFin());
        dto.setActif(year.isActif());
        return dto;
    }
}