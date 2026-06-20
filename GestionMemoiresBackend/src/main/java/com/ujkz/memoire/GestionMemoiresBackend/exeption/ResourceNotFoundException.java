package com.ujkz.memoire.GestionMemoiresBackend.exeption;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}