package com.example.anydeskapi.data.repositories;

import com.example.anydeskapi.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    List<UserEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    List<UserEntity> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(String username, String email, Pageable pageable);
}
