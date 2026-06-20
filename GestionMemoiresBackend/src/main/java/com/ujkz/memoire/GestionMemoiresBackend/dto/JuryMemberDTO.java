package com.ujkz.memoire.GestionMemoiresBackend.dto;

import com.ujkz.memoire.GestionMemoiresBackend.enums.JuryRole;
import lombok.Data;

@Data
public class JuryMemberDTO {
    private Long id;
    private Long juryId;
    private Long teacherId;
    private String teacherNom;
    private String teacherGrade;
    private String teacherSpecialite;
    private JuryRole role;
    private String roleLibelle;
    private Boolean present;
    private String remarques;
}