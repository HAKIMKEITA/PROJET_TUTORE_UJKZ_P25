package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.MilestoneDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Milestone;

import java.util.List;

public interface MilestoneService {
    List<MilestoneDTO> getMilestonesByMemoire(Long memoireId);
    List<MilestoneDTO> getPendingMilestones(Long memoireId);
    MilestoneDTO createMilestone(MilestoneDTO milestoneDTO);
    MilestoneDTO completeMilestone(Long milestoneId, String userEmail);
    MilestoneDTO updateMilestone(Long milestoneId, MilestoneDTO milestoneDTO);
    void deleteMilestone(Long milestoneId);
    Milestone getMilestoneEntity(Long id);
}