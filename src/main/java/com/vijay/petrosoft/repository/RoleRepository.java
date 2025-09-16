package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    List<Role> findByRoleType(Role.RoleType roleType);
}