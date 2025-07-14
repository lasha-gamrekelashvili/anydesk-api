package com.example.anydeskapi.data.specifications;

import com.example.anydeskapi.data.entities.TaskEntity;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {
    public static Specification<TaskEntity> hasTitle(String title) {
        return (root, query, cb) ->
            title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<TaskEntity> hasDescription(String description) {
        return (root, query, cb) ->
            description == null ? null : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }
}
