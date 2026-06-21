package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.GradeMemoireDTO;

public interface GradeService {
    GradeMemoireDTO saveGrade(Long defenseId, GradeMemoireDTO gradeDTO, String userEmail);
    GradeMemoireDTO getGradeByDefense(Long defenseId);
}