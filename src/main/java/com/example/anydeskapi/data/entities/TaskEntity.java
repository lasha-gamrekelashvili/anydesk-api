package com.example.anydeskapi.data.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "tasks")
@Data
public class TaskEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		private String title;

		private String description;

		private boolean completed;

		@ManyToMany(mappedBy = "tasks")
		private List<UserEntity> assignedUsers;
}
