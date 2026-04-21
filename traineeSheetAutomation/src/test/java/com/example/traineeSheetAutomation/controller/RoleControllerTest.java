package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.RoleRequestDTO;
import com.example.traineeSheetAutomation.dto.RoleResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.RoleName;
import com.example.traineeSheetAutomation.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RoleService roleService;

    private RoleResponseDTO sampleResponse;
    private RoleRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleResponse = RoleResponseDTO.builder()
                .roleId(1L)
                .roleName(RoleName.TRAINEE)
                .build();

        sampleRequest = new RoleRequestDTO(RoleName.TRAINEE);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createRole_whenCreated_returns201() throws Exception {
        when(roleService.createRole(any(RoleRequestDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleId").value(1))
                .andExpect(jsonPath("$.roleName").value("TRAINEE"));
    }

    @Test
    void createRole_whenNullRoleName_returns400() throws Exception {
        RoleRequestDTO badRequest = new RoleRequestDTO(null);

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).createRole(any());
    }

    @Test
    void createRole_whenInvalidRoleName_returns400() throws Exception {
        String invalidBody = "{\"roleName\": \"INVALID_ROLE\"}";

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidBody))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).createRole(any());
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllRoles_whenSuccessful_returns200() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].roleName").value("TRAINEE"));
    }

    @Test
    void getAllRoles_whenEmptyList_returns200() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getRoleById_whenSuccessful_returns200() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value(1))
                .andExpect(jsonPath("$.roleName").value("TRAINEE"));
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    @Test
    void updateRole_whenUpdated_returns200() throws Exception {
        when(roleService.updateRole(eq(1L), any(RoleRequestDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/roles/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value(1))
                .andExpect(jsonPath("$.roleName").value("TRAINEE"));
    }

    @Test
    void updateRole_whenNullRoleName_returns400() throws Exception {
        RoleRequestDTO badRequest = new RoleRequestDTO(null);

        mockMvc.perform(put("/api/v1/roles/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).updateRole(anyLong(), any());
    }

    @Test
    void updateRole_whenInvalidRoleName_returns400() throws Exception {
        String invalidBody = "{\"roleName\": \"INVALID_ROLE\"}";

        mockMvc.perform(put("/api/v1/roles/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidBody))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).updateRole(anyLong(), any());
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteRole_whenDeleted_returns200() throws Exception {
        doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(delete("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Role deleted successfully"));
    }
}