package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.RoleRequestDTO;
import com.example.traineeSheetAutomation.dto.RoleResponseDTO;
import com.example.traineeSheetAutomation.entity.Role;
import com.example.traineeSheetAutomation.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleResponseDTO createRole(RoleRequestDTO request) {

        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new RuntimeException("Role already exists: " + request.getRoleName());
        }

        Role role = new Role();
        role.setRoleName(request.getRoleName());

        return convertToDTO(roleRepository.save(role));
    }

    public List<RoleResponseDTO> getAllRoles() {

        List<RoleResponseDTO> roles = roleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            throw new RuntimeException("No roles found");
        }

        return roles;
    }

    public RoleResponseDTO getRoleById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Role not found with ID: " + id));

        return convertToDTO(role);
    }

    public RoleResponseDTO updateRole(Long id, RoleRequestDTO request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Role not found with ID: " + id));

        if (!role.getRoleName().equals(request.getRoleName()) &&
                roleRepository.existsByRoleName(request.getRoleName())) {
            throw new RuntimeException("Role already exists: " + request.getRoleName());
        }

        role.setRoleName(request.getRoleName());

        return convertToDTO(roleRepository.save(role));
    }

    public void deleteRole(Long id) {

        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with ID: " + id);
        }

        roleRepository.deleteById(id);
    }

    private RoleResponseDTO convertToDTO(Role role) {
        return RoleResponseDTO.builder()
                .roleId(role.getRoleID())
                .roleName(role.getRoleName())
                .build();
    }
}