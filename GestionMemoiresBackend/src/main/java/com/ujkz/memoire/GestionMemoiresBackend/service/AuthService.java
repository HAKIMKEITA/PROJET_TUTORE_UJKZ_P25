package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.LoginRequest;
import com.ujkz.memoire.GestionMemoiresBackend.dto.LoginResponse;

public interface AuthService {
    LoginResponse authenticate(LoginRequest loginRequest);
}