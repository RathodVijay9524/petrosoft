package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.User;
import com.vijay.petrosoft.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO registerUser(UserDTO.RegistrationRequest registrationRequest);
    UserDTO updateUser(Long id, UserDTO userDTO);
    Optional<UserDTO> getUserById(Long id);
    List<UserDTO> getAllUsers();
    void deleteUser(Long id);
    Optional<UserDTO> getUserByUsername(String username);
    UserDTO authenticateUser(String username, String password);
    List<UserDTO> getUsersByPumpId(Long pumpId);
    boolean isUsernameExists(String username);
    UserDTO changePassword(Long userId, String oldPassword, String newPassword);
}
