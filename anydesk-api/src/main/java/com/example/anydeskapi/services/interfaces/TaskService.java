package com.example.anydeskapi.services.interfaces;

import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(TaskRequestDto requestDto);

    Page<TaskResponseDto> getAllTasks(int page, int size, String title, String description);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskRequestDto requestDto);

    void deleteTask(Long id);
}
