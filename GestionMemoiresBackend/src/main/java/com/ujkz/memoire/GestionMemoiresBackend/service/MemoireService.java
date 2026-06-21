package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.MemoireDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.enums.AvancementStatus;

import java.util.List;

public interface MemoireService {
    MemoireDTO getMemoireByStudent(Long studentId);
    MemoireDTO getMemoireBySubject(Long subjectId);
    List<MemoireDTO> getAllMemoires();
    MemoireDTO createMemoireFromApplication(Long applicationId);
    MemoireDTO updateAvancement(Long memoireId, AvancementStatus statut);
    MemoireDTO validateSoutenabilite(Long memoireId, Boolean soutenable, String commentaire);
    List<MemoireDTO> getSoutenables();
    Memoire getMemoireEntity(Long id);
    MemoireStats getStats();
    
    class MemoireStats {
        public long total;
        public long soutenables;
        public long nonSoutenus;
        
        public MemoireStats(long total, long soutenables, long nonSoutenus) {
            this.total = total;
            this.soutenables = soutenables;
            this.nonSoutenus = nonSoutenus;
        }
    }
}