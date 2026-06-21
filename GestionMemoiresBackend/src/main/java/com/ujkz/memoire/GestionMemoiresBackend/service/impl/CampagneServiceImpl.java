package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.CampagneDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Campagne;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Semester;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.CampagneRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SemesterRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.CampagneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CampagneServiceImpl implements CampagneService {

    @Autowired
    private CampagneRepository campagneRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public List<CampagneDTO> getAllCampagnes() {
        return campagneRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CampagneDTO getActiveCampagne() {
        Campagne campagne = campagneRepository.findByOuverteTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Aucune campagne active"));
        return convertToDTO(campagne);
    }

    @Override
    public CampagneDTO getCampagneById(Long id) {
        Campagne campagne = getCampagneEntity(id);
        return convertToDTO(campagne);
    }

    @Override
    public CampagneDTO createCampagne(CampagneDTO campagneDTO) {
        Semester semester = semesterRepository.findById(campagneDTO.getSemestreId())
                .orElseThrow(() -> new ResourceNotFoundException("Semestre non trouvé"));

        if (campagneRepository.findByOuverteTrue().isPresent() && 
            campagneDTO.getOuverte() != null && campagneDTO.getOuverte()) {
            throw new BusinessException("Une campagne est déjà ouverte");
        }

        Campagne campagne = new Campagne();
        campagne.setLibelle(campagneDTO.getLibelle());
        campagne.setSemestre(semester);
        campagne.setDateDebutPropositionSujets(campagneDTO.getDateDebutPropositionSujets());
        campagne.setDateFinPropositionSujets(campagneDTO.getDateFinPropositionSujets());
        campagne.setDateDebutPositionnementEtudiants(campagneDTO.getDateDebutPositionnementEtudiants());
        campagne.setDateFinPositionnementEtudiants(campagneDTO.getDateFinPositionnementEtudiants());
        campagne.setDateLimiteDepotDocuments(campagneDTO.getDateLimiteDepotDocuments());
        campagne.setDateDebutSoutenances(campagneDTO.getDateDebutSoutenances());
        campagne.setDateFinSoutenances(campagneDTO.getDateFinSoutenances());
        campagne.setOuverte(campagneDTO.getOuverte() != null ? campagneDTO.getOuverte() : false);
        campagne.setActive(true);

        Campagne saved = campagneRepository.save(campagne);
        return convertToDTO(saved);
    }

    @Override
    public CampagneDTO toggleCampagne(Long id) {
        Campagne campagne = getCampagneEntity(id);

        if (!campagne.getOuverte() && campagneRepository.findByOuverteTrue().isPresent()) {
            throw new BusinessException("Une campagne est déjà ouverte");
        }

        campagne.setOuverte(!campagne.getOuverte());
        Campagne saved = campagneRepository.save(campagne);
        return convertToDTO(saved);
    }

    @Override
    public CampagneDTO updateCampagne(Long id, CampagneDTO campagneDTO) {
        Campagne campagne = getCampagneEntity(id);

        if (campagneDTO.getLibelle() != null) campagne.setLibelle(campagneDTO.getLibelle());
        if (campagneDTO.getDateDebutPropositionSujets() != null) 
            campagne.setDateDebutPropositionSujets(campagneDTO.getDateDebutPropositionSujets());
        if (campagneDTO.getDateFinPropositionSujets() != null) 
            campagne.setDateFinPropositionSujets(campagneDTO.getDateFinPropositionSujets());
        if (campagneDTO.getDateDebutPositionnementEtudiants() != null) 
            campagne.setDateDebutPositionnementEtudiants(campagneDTO.getDateDebutPositionnementEtudiants());
        if (campagneDTO.getDateFinPositionnementEtudiants() != null) 
            campagne.setDateFinPositionnementEtudiants(campagneDTO.getDateFinPositionnementEtudiants());
        if (campagneDTO.getDateLimiteDepotDocuments() != null) 
            campagne.setDateLimiteDepotDocuments(campagneDTO.getDateLimiteDepotDocuments());
        if (campagneDTO.getDateDebutSoutenances() != null) 
            campagne.setDateDebutSoutenances(campagneDTO.getDateDebutSoutenances());
        if (campagneDTO.getDateFinSoutenances() != null) 
            campagne.setDateFinSoutenances(campagneDTO.getDateFinSoutenances());

        Campagne saved = campagneRepository.save(campagne);
        return convertToDTO(saved);
    }

    @Override
    public void deleteCampagne(Long id) {
        Campagne campagne = getCampagneEntity(id);
        campagneRepository.delete(campagne);
    }

    @Override
    public Campagne getCampagneEntity(Long id) {
        return campagneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campagne non trouvée avec l'ID: " + id));
    }

    private CampagneDTO convertToDTO(Campagne campagne) {
        CampagneDTO dto = new CampagneDTO();
        dto.setId(campagne.getId());
        dto.setLibelle(campagne.getLibelle());
        dto.setSemestreId(campagne.getSemestre().getId());
        dto.setSemestreLibelle(campagne.getSemestre().getLibelle());
        dto.setDateDebutPropositionSujets(campagne.getDateDebutPropositionSujets());
        dto.setDateFinPropositionSujets(campagne.getDateFinPropositionSujets());
        dto.setDateDebutPositionnementEtudiants(campagne.getDateDebutPositionnementEtudiants());
        dto.setDateFinPositionnementEtudiants(campagne.getDateFinPositionnementEtudiants());
        dto.setDateLimiteDepotDocuments(campagne.getDateLimiteDepotDocuments());
        dto.setDateDebutSoutenances(campagne.getDateDebutSoutenances());
        dto.setDateFinSoutenances(campagne.getDateFinSoutenances());
        dto.setOuverte(campagne.getOuverte());
        dto.setActive(campagne.getActive());

        LocalDateTime now = LocalDateTime.now();
        if (campagne.getOuverte()) {
            if (now.isBefore(campagne.getDateDebutPropositionSujets())) {
                dto.setStatut("À venir");
            } else if (now.isAfter(campagne.getDateFinSoutenances())) {
                dto.setStatut("Terminée");
            } else {
                dto.setStatut("En cours");
            }
        } else {
            dto.setStatut("Fermée");
        }

        return dto;
    }
}