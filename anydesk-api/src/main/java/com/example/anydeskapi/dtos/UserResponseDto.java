package com.example.anydeskapi.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private List<Long> taskIds;
}
