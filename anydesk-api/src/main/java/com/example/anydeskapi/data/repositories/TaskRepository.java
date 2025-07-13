package com.example.anydeskapi.data.repositories;

import com.example.anydeskapi.data.entities.TaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<TaskEntity> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    List<TaskEntity> findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
}
