package com.example.anydeskapi.controllers;

import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;
import com.example.anydeskapi.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "CRUD operations for users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user with a unique email and username.")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "409", description = "User with this email already exists")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto createdUser = userService.createUser(requestDto);
        return ResponseEntity.status(201).body(createdUser);
    }

    @Operation(summary = "Get all users", description = "Retrieves a paginated list of users with optional filters.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
        @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Filter by username") @RequestParam(required = false) String username,
        @Parameter(description = "Filter by email") @RequestParam(required = false) String email) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, username, email));
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Update user", description = "Updates an existing user by ID.")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "Email already used by another user")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(id, requestDto));
    }

    @Operation(summary = "Delete user", description = "Deletes a user by ID.")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assign task to user", description = "Assigns a task to a user.")
    @ApiResponse(responseCode = "200", description = "Task assigned successfully")
    @ApiResponse(responseCode = "400", description = "Task already assigned")
    @ApiResponse(responseCode = "404", description = "User or task not found")
    @PatchMapping("/{userId}/assign-task/{taskId}")
    public ResponseEntity<Void> assignTaskToUser(@PathVariable Long userId, @PathVariable Long taskId) {
        userService.assignTaskToUser(userId, taskId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove task from user", description = "Removes a task from a user.")
    @ApiResponse(responseCode = "200", description = "Task removed successfully")
    @ApiResponse(responseCode = "400", description = "Task was not assigned to user")
    @ApiResponse(responseCode = "404", description = "User or task not found")
    @PatchMapping("/{userId}/remove-task/{taskId}")
    public ResponseEntity<Void> removeTaskFromUser(@PathVariable Long userId, @PathVariable Long taskId) {
        userService.removeTaskFromUser(userId, taskId);
        return ResponseEntity.ok().build();
    }
}



