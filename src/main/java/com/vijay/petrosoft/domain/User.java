package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false)
public class User extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    private String fullName;
    private String email;
    private String phone;
    @Builder.Default
    private boolean enabled = true;
    
    // User profile image
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "profile_image_data", columnDefinition = "LONGBLOB")
    private byte[] profileImageData;
    
    @Column(name = "image_content_type")
    private String imageContentType;
    
    // Additional user information
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 100)
    private String state;
    
    @Column(name = "pincode", length = 10)
    private String pincode;
    
    @Column(name = "emergency_contact", length = 15)
    private String emergencyContact;
    
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;
    
    @Column(name = "joining_date")
    private LocalDate joiningDate;
    
    @Column(name = "last_login")
    private java.time.LocalDateTime lastLogin;
    
    @Column(name = "login_attempts")
    @Builder.Default
    private Integer loginAttempts = 0;
    
    @Column(name = "account_locked")
    @Builder.Default
    private boolean accountLocked = false;
    
    @Column(name = "password_reset_token")
    private String passwordResetToken;
    
    @Column(name = "password_reset_expires")
    private java.time.LocalDateTime passwordResetExpires;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    private Long pumpId;
    
    // Helper methods
    public boolean isActive() {
        return enabled && !accountLocked;
    }
    
    public boolean hasProfileImage() {
        return profileImageUrl != null || (profileImageData != null && profileImageData.length > 0);
    }
}
