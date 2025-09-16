package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Optional<Role> findByRoleType(Role.RoleType roleType);
    boolean existsByName(String name);
}
