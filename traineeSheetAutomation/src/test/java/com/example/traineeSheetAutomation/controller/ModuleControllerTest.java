package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.ModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.ModuleResponseDTO;
import com.example.traineeSheetAutomation.service.ModuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ModuleController.class)
public class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ModuleService moduleService;

    private ModuleResponseDTO sampleResponse;
    private ModuleRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleResponse = ModuleResponseDTO.builder()
                .moduleId(1L)
                .templateId(1L)
                .templateTitle("JAVA")
                .moduleName("Core Java")
                .description("Basics of Java")
                .sequenceOrder(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = new ModuleRequestDTO(
                1L,
                "Core Java",
                "Basics of Java",
                1
        );
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createModule_whenCreated_returns201() throws Exception {
        when(moduleService.createModule(any(ModuleRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/modules")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.moduleId").value(1))
                .andExpect(jsonPath("$.moduleName").value("Core Java"))
                .andExpect(jsonPath("$.templateId").value(1));
    }

    @Test
    void createModule_whenNullModuleName_returns400() throws Exception {
        ModuleRequestDTO badRequest = new ModuleRequestDTO(1L, null, "Basics of Java", 1);

        mockMvc.perform(post("/api/v1/modules")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(moduleService, never()).createModule(any());
    }

    @Test
    void createModule_whenNullTemplateId_returns400() throws Exception {
        ModuleRequestDTO badRequest = new ModuleRequestDTO(null, "Core Java", "Basics of Java", 1);

        mockMvc.perform(post("/api/v1/modules")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(moduleService, never()).createModule(any());
    }

    @Test
    void createModule_whenNullSequenceOrder_returns400() throws Exception {
        ModuleRequestDTO badRequest = new ModuleRequestDTO(1L, "Core Java", "Basics of Java", null);

        mockMvc.perform(post("/api/v1/modules")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(moduleService, never()).createModule(any());
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllModules_whenSuccessful_returns200() throws Exception {
        when(moduleService.getAllModules()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].moduleName").value("Core Java"));
    }

    @Test
    void getAllModules_whenEmptyList_returns200() throws Exception {
        when(moduleService.getAllModules()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getModuleById_whenSuccessful_returns200() throws Exception {
        when(moduleService.getModuleById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleId").value(1))
                .andExpect(jsonPath("$.moduleName").value("Core Java"));
    }

    // ──────────────────────────── GET BY TEMPLATE ────────────────────────────

    @Test
    void getModulesByTemplate_whenSuccessful_returns200() throws Exception {
        when(moduleService.getModulesByTemplate(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/modules/template/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].templateId").value(1));
    }

    @Test
    void getModulesByTemplate_whenEmptyList_returns200() throws Exception {
        when(moduleService.getModulesByTemplate(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/modules/template/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    @Test
    void updateModule_whenUpdated_returns200() throws Exception {
        when(moduleService.updateModule(eq(1L), any(ModuleRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/modules/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleName").value("Core Java"))
                .andExpect(jsonPath("$.moduleId").value(1));
    }

    @Test
    void updateModule_whenNullModuleName_returns400() throws Exception {
        ModuleRequestDTO badRequest = new ModuleRequestDTO(1L, null, "Basics of Java", 1);

        mockMvc.perform(put("/api/v1/modules/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(moduleService, never()).updateModule(anyLong(), any());
    }

    @Test
    void updateModule_whenNullTemplateId_returns400() throws Exception {
        ModuleRequestDTO badRequest = new ModuleRequestDTO(null, "Core Java", "Basics of Java", 1);

        mockMvc.perform(put("/api/v1/modules/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(moduleService, never()).updateModule(anyLong(), any());
    }

    @Test
    void updateModule_whenNullSequenceOrder_returns400() throws Exception {
        ModuleRequestDTO badRequest = new ModuleRequestDTO(1L, "Core Java", "Basics of Java", null);

        mockMvc.perform(put("/api/v1/modules/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(moduleService, never()).updateModule(anyLong(), any());
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteModule_whenDeleted_returns200() throws Exception {
        doNothing().when(moduleService).deleteModule(1L);

        mockMvc.perform(delete("/api/v1/modules/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Module deleted successfully"));
    }
}
