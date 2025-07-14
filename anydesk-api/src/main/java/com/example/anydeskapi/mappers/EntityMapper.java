package com.example.anydeskapi.mappers;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.entities.UserEntity;
import com.example.anydeskapi.dtos.TaskRequestDto;
import com.example.anydeskapi.dtos.TaskResponseDto;
import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;

public class EntityMapper {
    public static UserResponseDto mapToDto(UserEntity user) {
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

    public static UserEntity mapToEntity(UserRequestDto dto) {
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static TaskResponseDto mapToDto(TaskEntity task) {
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

    public static TaskEntity mapToEntity(TaskRequestDto dto) {
        TaskEntity task = new TaskEntity();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        return task;
    }
}
