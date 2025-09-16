package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Role;
import com.vijay.petrosoft.domain.User;
import com.vijay.petrosoft.dto.UserDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.vijay.petrosoft.repository.RoleRepository;
import com.vijay.petrosoft.repository.UserRepository;
import com.vijay.petrosoft.service.RoleService;
import com.vijay.petrosoft.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (isUsernameExists(userDTO.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + userDTO.getUsername());
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .passwordHash(passwordEncoder.encode("defaultPassword"))
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .enabled(userDTO.isEnabled())
                .pumpId(userDTO.getPumpId())
                .roles(userDTO.getRoles())
                .joiningDate(LocalDate.now())
                .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO registerUser(UserDTO.RegistrationRequest registrationRequest) {
        log.info("Registering new user: {}", registrationRequest.getUsername());
        
        if (isUsernameExists(registrationRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + registrationRequest.getUsername());
        }

        if (isEmailExists(registrationRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + registrationRequest.getEmail());
        }

        // Initialize default roles if they don't exist
        roleService.initializeDefaultRoles();

        // Get the role for the user
        Role.RoleType roleType = registrationRequest.getRoleType() != null ? 
            registrationRequest.getRoleType() : Role.RoleType.OPERATOR;
        
        List<Role> roles = roleRepository.findByRoleType(roleType);
        if (roles.isEmpty()) {
            throw new RuntimeException("Role not found: " + roleType);
        }
        Role role = roles.get(0);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);

        User user = User.builder()
                .username(registrationRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registrationRequest.getPassword()))
                .fullName(registrationRequest.getFullName())
                .email(registrationRequest.getEmail())
                .phone(registrationRequest.getPhone())
                .enabled(true)
                .pumpId(registrationRequest.getPumpId())
                .roles(userRoles)
                .dateOfBirth(registrationRequest.getDateOfBirth())
                .address(registrationRequest.getAddress())
                .city(registrationRequest.getCity())
                .state(registrationRequest.getState())
                .pincode(registrationRequest.getPincode())
                .emergencyContact(registrationRequest.getEmergencyContact())
                .emergencyContactName(registrationRequest.getEmergencyContactName())
                .joiningDate(registrationRequest.getJoiningDate() != null ? 
                    registrationRequest.getJoiningDate() : LocalDate.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user: {} with role: {}", savedUser.getUsername(), roleType);
        
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO.UpdateRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFullName(updateRequest.getFullName());
        user.setEmail(updateRequest.getEmail());
        user.setPhone(updateRequest.getPhone());
        user.setEnabled(updateRequest.isEnabled());
        user.setPumpId(updateRequest.getPumpId());
        user.setDateOfBirth(updateRequest.getDateOfBirth());
        user.setAddress(updateRequest.getAddress());
        user.setCity(updateRequest.getCity());
        user.setState(updateRequest.getState());
        user.setPincode(updateRequest.getPincode());
        user.setEmergencyContact(updateRequest.getEmergencyContact());
        user.setEmergencyContactName(updateRequest.getEmergencyContactName());
        
        // Handle profile image
        if (updateRequest.getProfileImageData() != null) {
            user.setProfileImageData(updateRequest.getProfileImageData());
            user.setImageContentType(updateRequest.getImageContentType());
        }
        if (updateRequest.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateRequest.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByPumpId(Long pumpId) {
        return userRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsers() {
        return userRepository.findByEnabledTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getInactiveUsers() {
        return userRepository.findByEnabledFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String roleType) {
        return userRepository.findByRolesRoleType(Role.RoleType.valueOf(roleType)).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            // Increment login attempts
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() >= 5) {
                user.setAccountLocked(true);
                log.warn("Account locked for user: {} due to too many failed login attempts", username);
            }
            userRepository.save(user);
            throw new RuntimeException("Invalid username or password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        if (user.isAccountLocked()) {
            throw new RuntimeException("User account is locked");
        }

        // Reset login attempts on successful login
        user.setLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDTO changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        log.info("Password changed for user: {}", user.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    public UserDTO changePassword(Long userId, UserDTO.PasswordChangeRequest passwordChangeRequest) {
        return changePassword(userId, passwordChangeRequest.getCurrentPassword(), 
                            passwordChangeRequest.getNewPassword());
    }

    @Override
    public UserDTO activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        log.info("Activated user: {}", user.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    public UserDTO deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        log.info("Deactivated user: {}", user.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    public UserDTO lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setAccountLocked(true);
        User updatedUser = userRepository.save(user);
        log.info("Locked user: {}", user.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    public UserDTO unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setAccountLocked(false);
        user.setLoginAttempts(0);
        User updatedUser = userRepository.save(user);
        log.info("Unlocked user: {}", user.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    public UserDTO resetLoginAttempts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setLoginAttempts(0);
        User updatedUser = userRepository.save(user);
        log.info("Reset login attempts for user: {}", user.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    public UserDTO uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        try {
            user.setProfileImageData(file.getBytes());
            user.setImageContentType(file.getContentType());
            User updatedUser = userRepository.save(user);
            log.info("Profile image uploaded for user: {}", user.getUsername());
            return convertToDTO(updatedUser);
        } catch (IOException e) {
            log.error("Error uploading profile image for user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to upload profile image", e);
        }
    }

    @Override
    public UserDTO updateProfileImageUrl(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setProfileImageUrl(imageUrl);
        User updatedUser = userRepository.save(user);
        log.info("Profile image URL updated for user: {}", user.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setProfileImageData(null);
        user.setProfileImageUrl(null);
        user.setImageContentType(null);
        userRepository.save(user);
        log.info("Profile image deleted for user: {}", user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return user.getProfileImageData();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(String searchTerm) {
        return userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByCity(String city) {
        return userRepository.findByCityIgnoreCase(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByState(String state) {
        return userRepository.findByStateIgnoreCase(state).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByEnabledTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long getInactiveUserCount() {
        return userRepository.countByEnabledFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(String roleType) {
        return userRepository.countByRolesRoleType(Role.RoleType.valueOf(roleType));
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .pumpId(user.getPumpId())
                .roles(user.getRoles())
                .profileImageUrl(user.getProfileImageUrl())
                .profileImageData(user.getProfileImageData())
                .imageContentType(user.getImageContentType())
                .hasProfileImage(user.hasProfileImage())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .pincode(user.getPincode())
                .emergencyContact(user.getEmergencyContact())
                .emergencyContactName(user.getEmergencyContactName())
                .joiningDate(user.getJoiningDate())
                .lastLogin(user.getLastLogin())
                .loginAttempts(user.getLoginAttempts())
                .accountLocked(user.isAccountLocked())
                .isActive(user.isActive())
                .build();
    }
}