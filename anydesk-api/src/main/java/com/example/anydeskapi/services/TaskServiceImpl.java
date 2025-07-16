package com.example.anydeskapi.services;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.data.specifications.TaskSpecifications;
import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;
import com.example.anydeskapi.mappers.EntityMapper;
import com.example.anydeskapi.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        log.info("Received request to create task with title='{}'", requestDto.getTitle());

        boolean titleExists = taskRepository.findAll().stream()
            .anyMatch(t -> t.getTitle().equalsIgnoreCase(requestDto.getTitle()));
        if (titleExists) {
            log.warn("Cannot create task. Title '{}' is already in use.", requestDto.getTitle());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task with this title already exists.");
        }

        TaskEntity task = EntityMapper.mapToEntity(requestDto);
        TaskEntity saved = taskRepository.save(task);

        log.info("Task created successfully with ID={}", saved.getId());
        return EntityMapper.mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getAllTasks(int page, int size, String title, String description) {
        log.info("Fetching tasks with filters: title='{}', description='{}', page={}, size={}", title, description, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Specification<TaskEntity> spec = TaskSpecifications.hasTitle(title)
            .and(TaskSpecifications.hasDescription(description));

        Page<TaskEntity> taskPage = taskRepository.findAll(spec, pageable);

        log.info("Retrieved {} task(s) matching filters", taskPage.getTotalElements());
        return taskPage.map(EntityMapper::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        log.info("Looking up task by ID={}", id);

        TaskEntity task = taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("No task found with ID={}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
            });

        log.info("Task with ID={} retrieved successfully", id);
        return EntityMapper.mapToDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskRequestDto requestDto) {
        log.info("Updating task with ID={}", id);

        TaskEntity existing = taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Task not found for update. ID={}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
            });

        boolean titleUsedByAnother = taskRepository.findAll().stream()
            .anyMatch(t -> !t.getId().equals(id) && t.getTitle().equalsIgnoreCase(requestDto.getTitle()));
        if (titleUsedByAnother) {
            log.warn("Cannot update task. Title '{}' is already used by another task.", requestDto.getTitle());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another task with this title already exists.");
        }

        log.info("Task changes for ID={}: title='{}' → '{}', description='{}' → '{}'",
            id,
            existing.getTitle(), requestDto.getTitle(),
            existing.getDescription(), requestDto.getDescription());

        existing.setTitle(requestDto.getTitle());
        existing.setDescription(requestDto.getDescription());
        TaskEntity updated = taskRepository.save(existing);

        log.info("Task with ID={} updated successfully", updated.getId());
        return EntityMapper.mapToDto(updated);
    }


    @Override
    public void deleteTask(Long id) {
        log.info("Attempting to delete task with ID={}", id);

        TaskEntity existing = taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Task not found for deletion. ID={}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
            });

        int assignedCount = existing.getAssignedUsers().size();
        if (assignedCount > 0) {
            log.warn("Cannot delete task with ID={}. It is currently assigned to {} user(s)", id, assignedCount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete task: it is assigned to one or more users.");
        }

        taskRepository.delete(existing);
        log.info("Task with ID={} deleted successfully", id);
    }
}
