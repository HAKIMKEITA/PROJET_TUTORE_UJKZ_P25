package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.LoginRequest;
import com.ujkz.memoire.GestionMemoiresBackend.dto.LoginResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.exeption.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.security.JwtTokenProvider;
import com.ujkz.memoire.GestionMemoiresBackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return new LoginResponse(jwt, user.getId(), user.getEmail(),
                user.getNom(), user.getPrenom(), user.getRole().name());
    }
}