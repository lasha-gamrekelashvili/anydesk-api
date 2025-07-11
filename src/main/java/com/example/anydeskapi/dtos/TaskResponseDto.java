package com.example.anydeskapi.dtos;

import lombok.Data;

import java.util.List;

@Data
public class TaskResponseDto {
		private Long id;
		private String title;
		private String description;
		private boolean completed;
		private List<Long> assignedUserIds;
}
