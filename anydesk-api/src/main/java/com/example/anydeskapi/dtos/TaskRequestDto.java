package com.example.anydeskapi.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDto {

    @NotBlank(message = "Task title cannot be empty.")
    private String title;

    @NotBlank(message = "Task description cannot be empty.")
    private String description;
}
