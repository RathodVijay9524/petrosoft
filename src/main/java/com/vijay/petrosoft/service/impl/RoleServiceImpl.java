package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Role;
import com.vijay.petrosoft.repository.RoleRepository;
import com.vijay.petrosoft.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        log.info("Creating role: {}", role.getName());
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByType(Role.RoleType roleType) {
        List<Role> roles = roleRepository.findByRoleType(roleType);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRole(Long id, Role role) {
        log.info("Updating role with id: {}", id);
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        existingRole.setName(role.getName());
        existingRole.setRoleType(role.getRoleType());
        existingRole.setDescription(role.getDescription());
        
        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public void initializeDefaultRoles() {
        log.info("Initializing default roles");
        
        for (Role.RoleType roleType : Role.RoleType.values()) {
            if (!roleRepository.existsByName(roleType.name())) {
                Role role = Role.builder()
                        .name(roleType.name())
                        .roleType(roleType)
                        .description(roleType.getDescription())
                        .build();
                
                roleRepository.save(role);
                log.info("Created default role: {}", roleType.name());
            }
        }
    }
}
