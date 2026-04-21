package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.RoleRequestDTO;
import com.example.traineeSheetAutomation.dto.RoleResponseDTO;
import com.example.traineeSheetAutomation.entity.Role;
import com.example.traineeSheetAutomation.entity.enums.RoleName;
import com.example.traineeSheetAutomation.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role sampleRole;
    private RoleRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleRole = new Role();
        sampleRole.setRoleID(1L);
        sampleRole.setRoleName(RoleName.TRAINEE);

        sampleRequest = new RoleRequestDTO(RoleName.TRAINEE);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createRole_whenCreated_returnsSuccess() {
        when(roleRepository.existsByRoleName(RoleName.TRAINEE)).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(sampleRole);

        RoleResponseDTO result = roleService.createRole(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getRoleId()).isEqualTo(1L);
        assertThat(result.getRoleName()).isEqualTo(RoleName.TRAINEE);

        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_whenRoleNameAlreadyExists_throwsException() {
        when(roleRepository.existsByRoleName(RoleName.TRAINEE)).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role already exists: TRAINEE");

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void createRole_mapsRoleNameCorrectly() {
        when(roleRepository.existsByRoleName(RoleName.TRAINEE)).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(sampleRole);

        RoleResponseDTO result = roleService.createRole(sampleRequest);

        assertThat(result.getRoleName()).isEqualTo(RoleName.TRAINEE);
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllRoles_whenSuccessful_returnsList() {
        when(roleRepository.findAll()).thenReturn(List.of(sampleRole));

        List<RoleResponseDTO> result = roleService.getAllRoles();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoleName()).isEqualTo(RoleName.TRAINEE);
    }

    @Test
    void getAllRoles_whenEmpty_throwsException() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> roleService.getAllRoles())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No roles found");
    }

    @Test
    void getAllRoles_whenMultipleExist_returnsAll() {
        Role manager = new Role();
        manager.setRoleID(2L);
        manager.setRoleName(RoleName.MANAGER);

        when(roleRepository.findAll()).thenReturn(List.of(sampleRole, manager));

        List<RoleResponseDTO> result = roleService.getAllRoles();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(RoleResponseDTO::getRoleName)
                .containsExactlyInAnyOrder(RoleName.TRAINEE, RoleName.MANAGER);
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getRoleById_whenExists_returnsRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));

        RoleResponseDTO result = roleService.getRoleById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getRoleId()).isEqualTo(1L);
        assertThat(result.getRoleName()).isEqualTo(RoleName.TRAINEE);
    }

    @Test
    void getRoleById_whenNotFound_throwsException() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found with ID: 99");
    }

    @Test
    void getRoleById_mapsAllFieldsCorrectly() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));

        RoleResponseDTO result = roleService.getRoleById(1L);

        assertThat(result.getRoleId()).isEqualTo(1L);
        assertThat(result.getRoleName()).isEqualTo(RoleName.TRAINEE);
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    @Test
    void updateRole_whenValidData_returnsSuccess() {
        RoleRequestDTO updateRequest = new RoleRequestDTO(RoleName.MANAGER);

        Role updatedRole = new Role();
        updatedRole.setRoleID(1L);
        updatedRole.setRoleName(RoleName.MANAGER);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));
        when(roleRepository.existsByRoleName(RoleName.MANAGER)).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        RoleResponseDTO result = roleService.updateRole(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getRoleName()).isEqualTo(RoleName.MANAGER);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void updateRole_whenSameRoleName_doesNotCheckDuplicate() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));
        when(roleRepository.save(any(Role.class))).thenReturn(sampleRole);

        RoleResponseDTO result = roleService.updateRole(1L, sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getRoleName()).isEqualTo(RoleName.TRAINEE);
        verify(roleRepository, never()).existsByRoleName(RoleName.TRAINEE);
    }

    @Test
    void updateRole_whenNotFound_throwsException() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.updateRole(99L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found with ID: 99");

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void updateRole_whenRoleNameTakenByAnother_throwsException() {
        RoleRequestDTO updateRequest = new RoleRequestDTO(RoleName.MANAGER);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));
        when(roleRepository.existsByRoleName(RoleName.MANAGER)).thenReturn(true);

        assertThatThrownBy(() -> roleService.updateRole(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role already exists: MANAGER");

        verify(roleRepository, never()).save(any(Role.class));
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteRole_whenExists_success() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRole_whenNotFound_throwsException() {
        when(roleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> roleService.deleteRole(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found with ID: 99");

        verify(roleRepository, never()).deleteById(anyLong());
    }
}