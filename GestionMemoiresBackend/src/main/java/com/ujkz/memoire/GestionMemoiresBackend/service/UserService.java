package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.UserDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.UserRole;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO toggleUserStatus(Long id);
    UserDTO changeUserRole(Long id, UserRole role);
    User getUserByEmail(String email);
    User getUserEntity(Long id);
}