package com.ujkz.memoire.GestionMemoiresBackend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private boolean actif;
}