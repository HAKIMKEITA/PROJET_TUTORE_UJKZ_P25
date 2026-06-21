package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.UserDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.UserRole;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = getUserEntity(id);
        return convertToDTO(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("Un utilisateur avec cet email existe déjà");
        }

        User user = new User();
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(UserRole.valueOf(userDTO.getRole()));
        user.setActif(true);

        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = getUserEntity(id);

        if (userDTO.getNom() != null) user.setNom(userDTO.getNom());
        if (userDTO.getPrenom() != null) user.setPrenom(userDTO.getPrenom());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserEntity(id);
        userRepository.delete(user);
    }

    @Override
    public UserDTO toggleUserStatus(Long id) {
        User user = getUserEntity(id);
        user.setActif(!user.isActif());
        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    @Override
    public UserDTO changeUserRole(Long id, UserRole role) {
        User user = getUserEntity(id);
        user.setRole(role);
        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }

    @Override
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
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