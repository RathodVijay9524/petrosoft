package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Username cannot be empty")
    private String username;
    
    private String password; // Only for registration/update, not returned in responses
    
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private boolean enabled;
    private Set<Role> roles;
    private Long pumpId;
    
    // Profile image fields
    private String profileImageUrl;
    private byte[] profileImageData;
    private String imageContentType;
    private boolean hasProfileImage;
    
    // Additional user information
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String emergencyContact;
    private String emergencyContactName;
    private LocalDate joiningDate;
    private java.time.LocalDateTime lastLogin;
    private Integer loginAttempts;
    private boolean accountLocked;
    private boolean isActive;
    
    // For registration
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegistrationRequest {
        @NotBlank(message = "Username cannot be empty")
        private String username;
        
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String password;
        
        private String fullName;
        
        @Email(message = "Invalid email format")
        private String email;
        
        private String phone;
        private Long pumpId;
        private Role.RoleType roleType;
        
        // Additional fields for registration
        private LocalDate dateOfBirth;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private String emergencyContact;
        private String emergencyContactName;
        private LocalDate joiningDate;
    }
    
    // For user update
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String fullName;
        private String email;
        private String phone;
        private boolean enabled;
        private Long pumpId;
        
        // Profile image
        private String profileImageUrl;
        private byte[] profileImageData;
        private String imageContentType;
        
        // Additional user information
        private LocalDate dateOfBirth;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private String emergencyContact;
        private String emergencyContactName;
    }
    
    // For password change
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordChangeRequest {
        @NotBlank(message = "Current password cannot be empty")
        private String currentPassword;
        
        @NotBlank(message = "New password cannot be empty")
        @Size(min = 6, message = "New password must be at least 6 characters long")
        private String newPassword;
    }
}