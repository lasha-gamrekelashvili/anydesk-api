package com.example.anydeskapi.controllers;

import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;
import com.example.anydeskapi.services.interfaces.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "CRUD operations for tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDto requestDto) {
        TaskResponseDto createdTask = taskService.createTask(requestDto);
        return ResponseEntity.status(201).body(createdTask);
    }

    @GetMapping
    public List<TaskResponseDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @RequestBody TaskRequestDto requestDto) {
        return ResponseEntity.ok(taskService.updateTask(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
