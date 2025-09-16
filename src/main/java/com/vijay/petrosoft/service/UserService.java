package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Basic CRUD operations
    UserDTO createUser(UserDTO userDTO);
    UserDTO registerUser(UserDTO.RegistrationRequest registrationRequest);
    UserDTO updateUser(Long id, UserDTO.UpdateRequest updateRequest);
    Optional<UserDTO> getUserById(Long id);
    List<UserDTO> getAllUsers();
    Page<UserDTO> getAllUsers(Pageable pageable);
    void deleteUser(Long id);
    
    // User lookup operations
    Optional<UserDTO> getUserByUsername(String username);
    List<UserDTO> getUsersByPumpId(Long pumpId);
    List<UserDTO> getActiveUsers();
    List<UserDTO> getInactiveUsers();
    List<UserDTO> getUsersByRole(String roleType);
    
    // Authentication and security
    UserDTO authenticateUser(String username, String password);
    boolean isUsernameExists(String username);
    boolean isEmailExists(String email);
    UserDTO changePassword(Long userId, String oldPassword, String newPassword);
    UserDTO changePassword(Long userId, UserDTO.PasswordChangeRequest passwordChangeRequest);
    
    // User status management
    UserDTO activateUser(Long userId);
    UserDTO deactivateUser(Long userId);
    UserDTO lockUser(Long userId);
    UserDTO unlockUser(Long userId);
    UserDTO resetLoginAttempts(Long userId);
    
    // Profile image management
    UserDTO uploadProfileImage(Long userId, MultipartFile file);
    UserDTO updateProfileImageUrl(Long userId, String imageUrl);
    void deleteProfileImage(Long userId);
    byte[] getProfileImage(Long userId);
    
    // Search and filtering
    List<UserDTO> searchUsers(String searchTerm);
    List<UserDTO> getUsersByCity(String city);
    List<UserDTO> getUsersByState(String state);
    
    // Statistics
    long getTotalUserCount();
    long getActiveUserCount();
    long getInactiveUserCount();
    long getUserCountByRole(String roleType);
}
