package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    private Long studentId;
    private Long subjectId;
    private String motivation;
}