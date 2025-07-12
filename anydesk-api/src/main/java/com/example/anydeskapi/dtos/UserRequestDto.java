package com.example.anydeskapi.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Username cannot be empty.")
    private String username;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email format is invalid.")
    private String email;
}
