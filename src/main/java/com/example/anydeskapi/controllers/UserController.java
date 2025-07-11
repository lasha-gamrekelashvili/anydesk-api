package com.example.anydeskapi.controllers;
import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;
import com.example.anydeskapi.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "CRUD operations for users")
public class UserController {
		private final UserService userService;

		@PostMapping
		public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto requestDto) {
				UserResponseDto createdUser = userService.createUser(requestDto);
				return ResponseEntity.status(201).body(createdUser);
		}

		@GetMapping
		public List<UserResponseDto> getAllUsers() {
				return userService.getAllUsers();
		}

		@GetMapping("/{id}")
		public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
				return ResponseEntity.ok(userService.getUserById(id));
		}

		@PutMapping("/{id}")
		public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto requestDto) {
				return ResponseEntity.ok(userService.updateUser(id, requestDto));
		}

		@DeleteMapping("/{id}")
		public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
				userService.deleteUser(id);
				return ResponseEntity.noContent().build();
		}
}


