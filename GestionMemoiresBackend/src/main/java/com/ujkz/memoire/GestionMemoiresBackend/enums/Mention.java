package com.ujkz.memoire.GestionMemoiresBackend.enums;

public enum Mention {
    PASSABLE("Passable"),
    ASSEZ_BIEN("Assez bien"),
    BIEN("Bien"),
    TRES_BIEN("Très bien"),
    EXCELLENT("Excellent");

    private final String libelle;

    Mention(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}