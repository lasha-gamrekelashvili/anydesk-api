package com.example.anydeskapi.services;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.entities.UserEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.data.repositories.UserRepository;
import com.example.anydeskapi.data.specifications.UserSpecifications;
import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;
import com.example.anydeskapi.mappers.EntityMapper;
import com.example.anydeskapi.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto requestDto) {
        log.info("Received request to create user with email='{}' and username='{}'", requestDto.getEmail(), requestDto.getUsername());

        boolean emailExists = userRepository.findAll().stream()
            .anyMatch(u -> u.getEmail().equalsIgnoreCase(requestDto.getEmail()));
        if (emailExists) {
            log.warn("Cannot create user. Email '{}' is already in use.", requestDto.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists.");
        }

        UserEntity user = EntityMapper.mapToEntity(requestDto);
        UserEntity saved = userRepository.save(user);

        log.info("Successfully created user with ID={}", saved.getId());
        return EntityMapper.mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(int page, int size, String username, String email) {
        log.info("Fetching users with filters: username='{}', email='{}', page={}, size={}", username, email, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Specification<UserEntity> spec = UserSpecifications.hasUsername(username)
            .and(UserSpecifications.hasEmail(email));
        Page<UserEntity> users = userRepository.findAll(spec, pageable);

        log.info("Retrieved {} user(s) matching filters", users.getTotalElements());
        return users.map(EntityMapper::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("Looking up user by ID={}", id);

        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("No user found with ID={}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            });

        log.info("User with ID={} retrieved successfully", id);
        return EntityMapper.mapToDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        log.info("Updating user with ID={}", id);

        UserEntity existing = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found for update. ID={}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            });

        boolean emailUsedByAnother = userRepository.findAll().stream()
            .anyMatch(u -> !u.getId().equals(id) && u.getEmail().equalsIgnoreCase(requestDto.getEmail()));
        if (emailUsedByAnother) {
            log.warn("Cannot update user. Email '{}' is already used by another user.", requestDto.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another user with this email already exists.");
        }

        log.info("User changes for ID={}: username='{}' → '{}', email='{}' → '{}'",
            id,
            existing.getUsername(), requestDto.getUsername(),
            existing.getEmail(), requestDto.getEmail());

        existing.setUsername(requestDto.getUsername());
        existing.setEmail(requestDto.getEmail());
        UserEntity updated = userRepository.save(existing);

        log.info("User with ID={} updated successfully", updated.getId());
        return EntityMapper.mapToDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with ID={}", id);

        UserEntity existing = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found for deletion. ID={}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            });

        userRepository.delete(existing);
        log.info("User with ID={} deleted successfully", id);
    }

    @Override
    public void assignTaskToUser(Long userId, Long taskId) {
        log.info("Attempting to assign Task ID={} to User ID={}", taskId, userId);

        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User not found for task assignment. User ID={}", userId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            });

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> {
                log.warn("Task not found for assignment. Task ID={}", taskId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
            });

        if (user.getTasks().contains(task)) {
            log.warn("Task ID={} is already assigned to User ID={}", taskId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is already assigned to the user.");
        }

        user.getTasks().add(task);
        userRepository.save(user);

        log.info("Task ID={} successfully assigned to User ID={}", taskId, userId);
    }

    @Override
    public void removeTaskFromUser(Long userId, Long taskId) {
        log.info("Attempting to remove Task ID={} from User ID={}", taskId, userId);

        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User not found for task removal. User ID={}", userId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            });

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> {
                log.warn("Task not found for removal. Task ID={}", taskId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
            });

        if (!user.getTasks().contains(task)) {
            log.warn("Cannot remove task. Task ID={} is not assigned to User ID={}", taskId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is not assigned to the user.");
        }

        user.getTasks().remove(task);
        userRepository.save(user);

        log.info("Task ID={} successfully removed from User ID={}", taskId, userId);
    }
}
