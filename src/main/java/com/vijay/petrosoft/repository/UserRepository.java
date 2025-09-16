package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.User;
import com.vijay.petrosoft.domain.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Basic user queries
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // User status queries
    List<User> findByEnabledTrue();
    List<User> findByEnabledFalse();
    long countByEnabledTrue();
    long countByEnabledFalse();
    
    // Role-based queries
    List<User> findByRolesRoleType(Role.RoleType roleType);
    long countByRolesRoleType(Role.RoleType roleType);
    
    // Location-based queries
    List<User> findByCityIgnoreCase(String city);
    List<User> findByStateIgnoreCase(String state);
    
    // Search queries
    List<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String username, String fullName, String email);
    
    // Pump-based queries
    List<User> findByPumpId(Long pumpId);
}
