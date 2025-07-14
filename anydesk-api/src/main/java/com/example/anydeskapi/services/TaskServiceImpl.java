package com.example.anydeskapi.services;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.entities.UserEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.data.specifications.TaskSpecifications;
import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;
import com.example.anydeskapi.mappers.EntityMapper;
import com.example.anydeskapi.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        if (taskRepository.findAll().stream()
            .anyMatch(t -> t.getTitle().equalsIgnoreCase(requestDto.getTitle()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task with this title already exists.");
        }

        TaskEntity task = EntityMapper.mapToEntity(requestDto);
        TaskEntity saved = taskRepository.save(task);
        return EntityMapper.mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getAllTasks(int page, int size, String title, String description) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<TaskEntity> spec = TaskSpecifications.hasTitle(title)
            .and(TaskSpecifications.hasDescription(description));

        Page<TaskEntity> taskPage = taskRepository.findAll(spec, pageable);

        return taskPage.map(EntityMapper::mapToDto);
    }



    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        TaskEntity task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return EntityMapper.mapToDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskRequestDto requestDto) {
        TaskEntity existing = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (taskRepository.findAll().stream()
            .anyMatch(t -> !t.getId().equals(id) && t.getTitle().equalsIgnoreCase(requestDto.getTitle()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Another task with this title already exists.");
        }

        existing.setTitle(requestDto.getTitle());
        existing.setDescription(requestDto.getDescription());
        TaskEntity updated = taskRepository.save(existing);

        return EntityMapper.mapToDto(updated);
    }


    @Override
    public void deleteTask(Long id) {
        TaskEntity existing = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.delete(existing);
    }
}
