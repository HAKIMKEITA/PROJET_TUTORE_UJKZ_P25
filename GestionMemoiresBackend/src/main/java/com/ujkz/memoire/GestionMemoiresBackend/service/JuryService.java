package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.JuryDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.JuryMemberDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Jury;

public interface JuryService {
    JuryDTO getJuryByDefense(Long defenseId);
    JuryDTO createJury(Long defenseId);
    JuryDTO addJuryMember(Long juryId, JuryMemberDTO memberDTO);
    void removeJuryMember(Long memberId);
    JuryDTO markMemberPresence(Long memberId, Boolean present);
    JuryDTO validateJury(Long juryId);
    Jury getJuryEntity(Long id);
}