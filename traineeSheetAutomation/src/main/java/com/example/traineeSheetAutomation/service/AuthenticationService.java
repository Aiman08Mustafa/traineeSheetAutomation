package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.AuthenticationRequestDTO;
import com.example.traineeSheetAutomation.dto.AuthenticationResponseDTO;
import com.example.traineeSheetAutomation.dto.RegisterRequestDTO;
import com.example.traineeSheetAutomation.entity.Role;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.RoleName;
import com.example.traineeSheetAutomation.repository.RoleRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
import com.example.traineeSheetAutomation.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        // Check if user already exists
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new RuntimeException("Email already registered");
        }

        // Fetch role entity from DB
        Role role = roleRepository.findByRoleName(RoleName.valueOf(request.getRole().toUpperCase()))
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));

        var user = new User();
        user.setName(request.getFirstName() + " " + request.getLastName());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        var savedUser = userRepository.save(user);

        // Generate token with role
        var jwtToken = jwtService.generateToken(savedUser);

        System.out.println("Registered user: " + savedUser.getEmail() + " with role: " + savedUser.getRole().getRoleName());

        return AuthenticationResponseDTO.builder()
                .userId(savedUser.getUserId())
                .name(savedUser.getName())
                .token(jwtToken)
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getRoleName().name())
                .build();
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate token with role
        var jwtToken = jwtService.generateToken(user);

        System.out.println("Authenticated user: " + user.getEmail() + " with role: " + user.getRole().getRoleName());

        return AuthenticationResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().getRoleName().name())
                .build();
    }
}