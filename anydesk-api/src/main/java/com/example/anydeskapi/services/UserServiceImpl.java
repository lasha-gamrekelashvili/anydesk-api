package com.example.anydeskapi.services;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.entities.UserEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.data.repositories.UserRepository;
import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;
import com.example.anydeskapi.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto requestDto) {
        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(requestDto.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists.");
        }

        UserEntity user = mapToEntity(requestDto);
        UserEntity saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Override
    public List<UserResponseDto> getAllUsers(int page, int size, String username, String email) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserEntity> users;

        if (username != null && email != null) {
            users = userRepository.findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(username, email, pageable);
        } else if (username != null) {
            users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        } else if (email != null) {
            users = userRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            users = userRepository.findAll(pageable).getContent();
        }

        return users.stream().map(this::mapToDto).toList();
    }


    @Override
    public UserResponseDto getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        UserEntity existing = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userRepository.findAll().stream()
            .anyMatch(u -> !u.getId().equals(id) && u.getEmail().equalsIgnoreCase(requestDto.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Another user with this email already exists.");
        }

        existing.setUsername(requestDto.getUsername());
        existing.setEmail(requestDto.getEmail());
        UserEntity updated = userRepository.save(existing);

        return mapToDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity existing = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(existing);
    }

    @Override
    public void assignTaskToUser(Long userId, Long taskId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (user.getTasks().contains(task)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is already assigned to the user.");
        }

        user.getTasks().add(task);
        userRepository.save(user);
    }

    @Override
    public void removeTaskFromUser(Long userId, Long taskId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (!user.getTasks().contains(task)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is not assigned to the user.");
        }

        user.getTasks().remove(task);
        userRepository.save(user);
    }

    private UserResponseDto mapToDto(UserEntity user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        if (user.getTasks() != null) {
            dto.setTaskIds(user.getTasks()
                .stream()
                .map(TaskEntity::getId)
                .toList());
        }
        return dto;
    }

    private UserEntity mapToEntity(UserRequestDto dto) {
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }
}
