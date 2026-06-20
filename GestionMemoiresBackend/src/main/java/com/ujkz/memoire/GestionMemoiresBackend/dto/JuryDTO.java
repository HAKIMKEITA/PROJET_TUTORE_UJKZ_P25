package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class JuryDTO {
    private Long id;
    private Long defenseId;
    private Integer nombreMembresMinimal;
    private Boolean constitue;
    private Boolean complet;
    private List<JuryMemberDTO> membres;
}