package com.example.anydeskapi.services;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.entities.UserEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;
import com.example.anydeskapi.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        TaskEntity task = mapToEntity(requestDto);
        TaskEntity saved = taskRepository.save(task);
        return mapToDto(saved);
    }

    @Override
    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        TaskEntity task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return mapToDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskRequestDto requestDto) {
        TaskEntity existing = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        existing.setTitle(requestDto.getTitle());
        existing.setDescription(requestDto.getDescription());
        TaskEntity updated = taskRepository.save(existing);

        return mapToDto(updated);
    }

    @Override
    public void deleteTask(Long id) {
        TaskEntity existing = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.delete(existing);
    }

    private TaskResponseDto mapToDto(TaskEntity task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());

        if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
            dto.setAssignedUserIds(
                task.getAssignedUsers()
                    .stream()
                    .map(UserEntity::getId)
                    .toList()
            );
        }
        return dto;
    }

    private TaskEntity mapToEntity(TaskRequestDto dto) {
        TaskEntity task = new TaskEntity();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        return task;
    }
}
