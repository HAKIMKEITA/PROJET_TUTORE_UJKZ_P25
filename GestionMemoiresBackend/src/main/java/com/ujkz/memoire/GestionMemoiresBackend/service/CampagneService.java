package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.CampagneDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Campagne;

import java.util.List;

public interface CampagneService {
    List<CampagneDTO> getAllCampagnes();
    CampagneDTO getActiveCampagne();
    CampagneDTO getCampagneById(Long id);
    CampagneDTO createCampagne(CampagneDTO campagneDTO);
    CampagneDTO toggleCampagne(Long id);
    CampagneDTO updateCampagne(Long id, CampagneDTO campagneDTO);
    Campagne getCampagneEntity(Long id);
}