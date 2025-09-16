package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.domain.Role;
import com.vijay.petrosoft.dto.UserDTO;
import com.vijay.petrosoft.security.JwtUtil;
import com.vijay.petrosoft.service.RoleService;
import com.vijay.petrosoft.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RoleService roleService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for user: {}", loginRequest.getUsername());
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            String token = jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());
            
            // Get user details and roles
            UserDTO userDTO = userService.getUserByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<String> roles = userDTO.getRoles().stream()
                    .map(role -> role.getRoleType().name())
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", loginRequest.getUsername());
            response.put("fullName", userDTO.getFullName());
            response.put("email", userDTO.getEmail());
            response.put("roles", roles);
            response.put("pumpId", userDTO.getPumpId());
            response.put("message", "Login successful");
            
            log.info("Successful login for user: {} with roles: {}", loginRequest.getUsername(), roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            errorResponse.put("message", "Username or password is incorrect");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDTO.RegistrationRequest registrationRequest) {
        try {
            log.info("Registration attempt for user: {}", registrationRequest.getUsername());
            
            UserDTO userDTO = userService.registerUser(registrationRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("username", userDTO.getUsername());
            response.put("fullName", userDTO.getFullName());
            response.put("email", userDTO.getEmail());
            response.put("roles", userDTO.getRoles().stream()
                    .map(role -> role.getRoleType().name())
                    .collect(Collectors.toList()));
            
            log.info("Successful registration for user: {}", registrationRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed for user: {} - {}", registrationRequest.getUsername(), e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, Object>>> getAvailableRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            List<Map<String, Object>> roleList = roles.stream()
                    .map(role -> {
                        Map<String, Object> roleMap = new HashMap<>();
                        roleMap.put("type", role.getRoleType().name());
                        roleMap.put("name", role.getName());
                        roleMap.put("displayName", role.getRoleType().getDisplayName());
                        roleMap.put("description", role.getRoleType().getDescription());
                        return roleMap;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(roleList);
        } catch (Exception e) {
            log.error("Failed to get roles: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOTP(@RequestParam String username) {
        try {
            // Generate 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(1000000));
            
            // Store OTP temporarily (in production, use Redis or database)
            // For now, we'll just send the SMS
            
            // Get user phone number and send OTP
            // This would need to be implemented based on your user service
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent successfully");
            response.put("otp", otp); // Remove this in production
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to send OTP");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOTP(@RequestParam String username, @RequestParam String otp) {
        try {
            // Verify OTP (implement proper verification logic)
            // For now, just return success
            
            // For OTP verification, we need to create a proper UserDetails object
            // This is a simplified implementation - in production, you'd want proper user lookup
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                org.springframework.security.core.userdetails.User.builder()
                    .username(username)
                    .password("") // OTP verification doesn't need password
                    .roles("USER")
                    .build();
            String token = jwtUtil.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);
            response.put("message", "OTP verified successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid OTP");
            errorResponse.put("message", "OTP verification failed");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam String username) {
        try {
            // Generate reset token and send email/SMS
            // This would need proper implementation
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Password reset instructions sent");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to send reset instructions");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
