package com.budgettrip.service;

import com.budgettrip.dto.UserRegistrationDto;
import com.budgettrip.entity.Role;
import com.budgettrip.entity.User;
import com.budgettrip.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(UserRegistrationDto dto) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setRole(Role.USER);
        user.setPreference(dto.getPreference());

        userRepository.save(user);
    }
}