package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.UserRequestDTO;
import com.example.traineeSheetAutomation.dto.UserResponseDTO;
import com.example.traineeSheetAutomation.entity.Role;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.repository.RoleRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
import com.example.traineeSheetAutomation.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordUtil passwordUtil;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException(
                        "Role not found with ID: " + request.getRoleId()));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        return convertToDTO(userRepository.save(user));
    }



    public List<UserResponseDTO> getAllUsers() {

        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            throw new RuntimeException("No users found");
        }

        return users;
    }


    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with ID: " + id));

        return convertToDTO(user);
    }


    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with ID: " + id));

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException(
                        "Role not found with ID: " + request.getRoleId()));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordUtil.hashPassword(request.getPassword()));
        user.setRole(role);

        return convertToDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
    }


    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole().getRoleName().name());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}