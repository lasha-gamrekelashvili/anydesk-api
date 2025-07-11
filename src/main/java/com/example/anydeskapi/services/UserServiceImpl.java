package com.example.anydeskapi.services;

import com.example.anydeskapi.data.repositories.interfaces.UserRepository;
import com.example.anydeskapi.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
		private final UserRepository userRepository;
}
