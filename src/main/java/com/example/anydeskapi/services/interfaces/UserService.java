package com.example.anydeskapi.services.interfaces;

import com.example.anydeskapi.dtos.UserRequestDto;
import com.example.anydeskapi.dtos.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto requestDto);

    void deleteUser(Long id);
}
