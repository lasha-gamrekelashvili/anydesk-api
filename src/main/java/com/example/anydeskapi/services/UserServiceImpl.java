package com.example.anydeskapi.services;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.entities.UserEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.data.repositories.UserRepository;
import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;
import com.example.anydeskapi.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto requestDto) {
        UserEntity user = mapToEntity(requestDto);
        UserEntity saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
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
            .orElseThrow(() -> new RuntimeException("User not found"));

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        user.getTasks().add(task);
        userRepository.save(user);
    }

    @Override
    public void removeTaskFromUser(Long userId, Long taskId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        TaskEntity task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

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
