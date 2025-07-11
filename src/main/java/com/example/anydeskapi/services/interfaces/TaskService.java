package com.example.anydeskapi.services.interfaces;

import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;

import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(TaskRequestDto requestDto);

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskRequestDto requestDto);

    void deleteTask(Long id);
}
