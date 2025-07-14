package com.example.anydeskapi.data.specifications;

import com.example.anydeskapi.data.entities.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    public static Specification<UserEntity> hasUsername(String username) {
        return (root, query, cb) ->
            username == null ? null : cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<UserEntity> hasEmail(String email) {
        return (root, query, cb) ->
            email == null ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
}
