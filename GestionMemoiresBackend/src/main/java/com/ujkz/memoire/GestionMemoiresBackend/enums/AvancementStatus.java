package com.ujkz.memoire.GestionMemoiresBackend.enums;

public enum AvancementStatus {
    EN_COURS("En cours de réalisation"),
    PLAN_VALIDE("Plan validé"),
    DOCUMENT_INTERMEDIAIRE("Document intermédiaire remis"),
    PRE_VALIDE("Pré-validé"),
    FINAL_RENDU("Mémoire final rendu"),
    SOUTENABLE("Soutenable"),
    TERMINE("Terminé");

    private final String libelle;

    AvancementStatus(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}