package com.example.anydeskapi.services;

import com.example.anydeskapi.data.repositories.interfaces.TaskRepository;
import com.example.anydeskapi.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
		private final TaskRepository taskRepository;
}
