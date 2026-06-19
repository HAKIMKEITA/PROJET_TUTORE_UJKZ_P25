package com.ujkz.memoire.GestionMemoiresBackend.exception;

public class InvalidGradeException extends BusinessRuleViolationException {
    public InvalidGradeException(String message) {
        super(message);
    }
}
