package com.ujkz.memoire.GestionMemoiresBackend.exception;

public class DuplicateApplicationException extends BusinessRuleViolationException {
    public DuplicateApplicationException(String message) {
        super(message);
    }
}
