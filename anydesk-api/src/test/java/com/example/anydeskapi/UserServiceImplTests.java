package com.example.anydeskapi;

import com.example.anydeskapi.data.entities.TaskEntity;
import com.example.anydeskapi.data.entities.UserEntity;
import com.example.anydeskapi.data.repositories.TaskRepository;
import com.example.anydeskapi.data.repositories.UserRepository;
import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;
import com.example.anydeskapi.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTests {

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        taskRepository = mock(TaskRepository.class);
        userService = new UserServiceImpl(userRepository, taskRepository);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        UserRequestDto dto = new UserRequestDto("John","john@example.com");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("John");
        user.setEmail("john@example.com");

        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(userRepository.save(any())).thenReturn(user);

        UserResponseDto result = userService.createUser(dto);

        assertEquals("John", result.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void assignTaskToUser_ShouldAssign_WhenValid() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setTasks(new ArrayList<>());

        TaskEntity task = new TaskEntity();
        task.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        userService.assignTaskToUser(1L, 2L);
        assertTrue(user.getTasks().contains(task));
    }

    @Test
    void assignTaskToUser_ShouldThrow_WhenAlreadyAssigned() {
        TaskEntity task = new TaskEntity();
        task.setId(2L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setTasks(new ArrayList<>(List.of(task)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        assertThrows(ResponseStatusException.class, () -> userService.assignTaskToUser(1L, 2L));
    }
}
