package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.UserDTO;
import com.vijay.petrosoft.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Comprehensive user management operations including CRUD, authentication, and profile management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    // Basic CRUD Operations
    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieve a list of all users in the system. Requires authentication and appropriate role permissions.",
            operationId = "getAllUsers"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all users",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "username": "admin",
                                                    "fullName": "System Administrator",
                                                    "email": "admin@petrosoft.com",
                                                    "phone": "+1234567890",
                                                    "enabled": true,
                                                    "roles": [{"roleType": "OWNER"}],
                                                    "pumpId": 1,
                                                    "isActive": true,
                                                    "hasProfileImage": false
                                                }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing authentication token",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unauthorized",
                                    value = """
                                            {
                                                "error": "Unauthorized",
                                                "message": "Invalid or missing authentication token"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Forbidden",
                                    value = """
                                            {
                                                "error": "Forbidden",
                                                "message": "Insufficient permissions to access user data"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                                            {
                                                "error": "Internal Server Error",
                                                "message": "An unexpected error occurred while retrieving users"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<UserDTO>> getAllUsersPaginated(Pageable pageable) {
        try {
            Page<UserDTO> users = userService.getAllUsers(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get paginated users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, 
                                            @Valid @RequestBody UserDTO.UpdateRequest updateRequest) {
        try {
            UserDTO updatedUser = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Failed to update user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // User Lookup Operations
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        try {
            Optional<UserDTO> user = userService.getUserByUsername(username);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to get user with username {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<UserDTO>> getUsersByPumpId(@PathVariable Long pumpId) {
        try {
            List<UserDTO> users = userService.getUsersByPumpId(pumpId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users for pump {}: {}", pumpId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        try {
            List<UserDTO> users = userService.getActiveUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get active users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<UserDTO>> getInactiveUsers() {
        try {
            List<UserDTO> users = userService.getInactiveUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get inactive users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/role/{roleType}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String roleType) {
        try {
            List<UserDTO> users = userService.getUsersByRole(roleType);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users by role {}: {}", roleType, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // User Status Management
    @PutMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        try {
            UserDTO user = userService.activateUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to activate user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        try {
            UserDTO user = userService.deactivateUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to deactivate user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<UserDTO> lockUser(@PathVariable Long id) {
        try {
            UserDTO user = userService.lockUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to lock user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<UserDTO> unlockUser(@PathVariable Long id) {
        try {
            UserDTO user = userService.unlockUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to unlock user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/reset-login-attempts")
    public ResponseEntity<UserDTO> resetLoginAttempts(@PathVariable Long id) {
        try {
            UserDTO user = userService.resetLoginAttempts(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to reset login attempts for user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Password Management
    @PutMapping("/{id}/change-password")
    public ResponseEntity<UserDTO> changePassword(@PathVariable Long id, 
                                                @Valid @RequestBody UserDTO.PasswordChangeRequest passwordChangeRequest) {
        try {
            UserDTO user = userService.changePassword(id, passwordChangeRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to change password for user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Profile Image Management
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<UserDTO> uploadProfileImage(@PathVariable Long id, 
                                                    @RequestParam("file") MultipartFile file) {
        try {
            UserDTO user = userService.uploadProfileImage(id, file);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to upload profile image for user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/profile-image-url")
    public ResponseEntity<UserDTO> updateProfileImageUrl(@PathVariable Long id, 
                                                       @RequestParam String imageUrl) {
        try {
            UserDTO user = userService.updateProfileImageUrl(id, imageUrl);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to update profile image URL for user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long id) {
        try {
            byte[] imageData = userService.getProfileImage(id);
            if (imageData != null && imageData.length > 0) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageData);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to get profile image for user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@PathVariable Long id) {
        try {
            userService.deleteProfileImage(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete profile image for user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search and Filtering
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String searchTerm) {
        try {
            List<UserDTO> users = userService.searchUsers(searchTerm);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to search users with term {}: {}", searchTerm, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<UserDTO>> getUsersByCity(@PathVariable String city) {
        try {
            List<UserDTO> users = userService.getUsersByCity(city);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users by city {}: {}", city, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<UserDTO>> getUsersByState(@PathVariable String state) {
        try {
            List<UserDTO> users = userService.getUsersByState(state);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users by state {}: {}", state, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Validation Endpoints
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = userService.isUsernameExists(username);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Failed to check username existence for {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = userService.isEmailExists(email);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Failed to check email existence for {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        try {
            Map<String, Long> stats = new HashMap<>();
            stats.put("totalUsers", userService.getTotalUserCount());
            stats.put("activeUsers", userService.getActiveUserCount());
            stats.put("inactiveUsers", userService.getInactiveUserCount());
            stats.put("ownerUsers", userService.getUserCountByRole("OWNER"));
            stats.put("managerUsers", userService.getUserCountByRole("MANAGER"));
            stats.put("operatorUsers", userService.getUserCountByRole("OPERATOR"));
            stats.put("accountantUsers", userService.getUserCountByRole("ACCOUNTANT"));
            stats.put("supportUsers", userService.getUserCountByRole("SUPPORT"));
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to get user statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}