package com.ujkz.memoire.GestionMemoiresBackend.enums;

public enum DocumentType {
    PLAN_MEMOIRE("Plan de mémoire"),
    RAPPORT_INTERMEDIAIRE("Rapport intermédiaire"),
    RAPPORT_FINAL("Rapport final"),
    PRESENTATION("Présentation"),
    AUTRE("Autre");

    private final String libelle;

    DocumentType(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}