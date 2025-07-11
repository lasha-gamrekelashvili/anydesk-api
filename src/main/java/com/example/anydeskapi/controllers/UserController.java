package com.example.anydeskapi.controllers;
import com.example.anydeskapi.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
		private final UserService userService;
}
