package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.UserDTO;
import com.vijay.petrosoft.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        try {
            Optional<UserDTO> user = userService.getUserById(id);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to get user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        try {
            Optional<UserDTO> user = userService.getUserByUsername(username);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to get user with username {}: {}", username, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<UserDTO>> getUsersByPumpId(@PathVariable Long pumpId) {
        try {
            List<UserDTO> users = userService.getUsersByPumpId(pumpId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users for pump {}: {}", pumpId, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Failed to update user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/exists/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = userService.isUsernameExists(username);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Failed to check username existence for {}: {}", username, e.getMessage());
            return ResponseEntity.status(500).body(false);
        }
    }
}