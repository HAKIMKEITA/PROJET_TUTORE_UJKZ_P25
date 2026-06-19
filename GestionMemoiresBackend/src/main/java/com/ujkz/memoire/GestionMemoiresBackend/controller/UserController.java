package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.UserDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Obtenir la liste des utilisateurs")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Obtenir les informations d'un utilisateur")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return ResponseEntity.ok(convertToDTO(user));
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        dto.setActif(user.isActif());
        return dto;
    }
}