package com.ujkz.memoire.GestionMemoiresBackend.enums;

public enum ObservationType {
    RECOMMANDATION("Recommandation"),
    CORRECTION("Correction"),
    REMARQUE("Remarque"),
    EVALUATION("Évaluation");

    private final String libelle;

    ObservationType(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}