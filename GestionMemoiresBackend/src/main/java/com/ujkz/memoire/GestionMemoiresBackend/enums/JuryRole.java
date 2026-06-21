package com.ujkz.memoire.GestionMemoiresBackend.enums;

public enum JuryRole {
    PRESIDENT("Président"),
    ENCADRANT("Encadrant"),
    SUPERVISEUR("Superviseur"),
    RAPPORTEUR("Rapporteur"),
    EXAMINATEUR("Examinateur");

    private final String libelle;

    JuryRole(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}