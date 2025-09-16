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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .passwordHash(passwordEncoder.encode("defaultPassword")) // Default password, should be set via separate method
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .enabled(userDTO.isEnabled())
                .pumpId(userDTO.getPumpId())
                .roles(userDTO.getRoles())
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

        // Initialize default roles if they don't exist
        roleService.initializeDefaultRoles();

        // Get the role for the user
        Role.RoleType roleType = registrationRequest.getRoleType() != null ? 
            registrationRequest.getRoleType() : Role.RoleType.OPERATOR;
        
        Role role = roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType));

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
                .build();

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user: {} with role: {}", savedUser.getUsername(), roleType);
        
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setEnabled(userDTO.isEnabled());
        user.setPumpId(userDTO.getPumpId());

        // Password updates should be done via changePassword method for security

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
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        return convertToDTO(user);
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
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDTO changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
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
                .build();
    }
}
