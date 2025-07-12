package com.example.anydeskapi;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;
import com.example.anydeskapi.services.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTests {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskServiceImpl(taskRepository);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        TaskRequestDto dto = new TaskRequestDto("Test Title", "Test Description");

        TaskEntity savedEntity = new TaskEntity();
        savedEntity.setId(1L);
        savedEntity.setTitle("Test Title");
        savedEntity.setDescription("Test Description");

        when(taskRepository.findAll()).thenReturn(new ArrayList<>());
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(savedEntity);

        TaskResponseDto result = taskService.createTask(dto);

        assertEquals("Test Title", result.getTitle());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void createTask_ShouldThrow_WhenTitleExists() {
        TaskRequestDto dto = new TaskRequestDto("Test","Desc");

        TaskEntity existing = new TaskEntity();
        existing.setTitle("Test");

        when(taskRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(ResponseStatusException.class, () -> taskService.createTask(dto));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        task.setTitle("T");
        task.setDescription("D");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDto dto = taskService.getTaskById(1L);
        assertEquals("T", dto.getTitle());
    }

    @Test
    void getTaskById_ShouldThrow_WhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> taskService.getTaskById(1L));
    }
}
