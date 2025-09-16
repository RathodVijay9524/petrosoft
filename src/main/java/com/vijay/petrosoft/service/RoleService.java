package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role createRole(Role role);
    Optional<Role> getRoleById(Long id);
    Optional<Role> getRoleByName(String name);
    Optional<Role> getRoleByType(Role.RoleType roleType);
    List<Role> getAllRoles();
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
    void initializeDefaultRoles();
}
