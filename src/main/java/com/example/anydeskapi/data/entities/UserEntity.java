package com.example.anydeskapi.data.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		private String username;

		private String email;

		@ManyToMany
		@JoinTable(
						name = "user_tasks",
						joinColumns = @JoinColumn(name = "user_id"),
						inverseJoinColumns = @JoinColumn(name = "task_id")
		)
		private List<TaskEntity> tasks;
}
