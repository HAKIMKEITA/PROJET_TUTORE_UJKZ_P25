package com.ujkz.memoire.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private boolean actif;
}