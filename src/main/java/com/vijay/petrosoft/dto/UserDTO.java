package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private boolean enabled;
    private Set<Role> roles;
    private Long pumpId;
    
    // For registration
    public static class RegistrationRequest {
        private String username;
        private String password;
        private String fullName;
        private String email;
        private String phone;
        private Long pumpId;
        private Role.RoleType roleType;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public Long getPumpId() { return pumpId; }
        public void setPumpId(Long pumpId) { this.pumpId = pumpId; }
        
        public Role.RoleType getRoleType() { return roleType; }
        public void setRoleType(Role.RoleType roleType) { this.roleType = roleType; }
    }
}