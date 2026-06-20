package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationRequest;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Application;

import java.util.List;

public interface ApplicationService {
    Application applyToSubject(ApplicationRequest request);
    List<ApplicationDTO> getApplicationsByStudent(Long studentId);
    List<ApplicationDTO> getApplicationsBySubject(Long subjectId);
    ApplicationDTO acceptApplication(Long applicationId, String commentaire);
    ApplicationDTO rejectApplication(Long applicationId, String commentaire);
    Application getApplicationEntity(Long id);
    List<ApplicationDTO> getAllApplications();
}