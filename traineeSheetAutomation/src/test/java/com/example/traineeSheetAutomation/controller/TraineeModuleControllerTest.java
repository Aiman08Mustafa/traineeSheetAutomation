package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TraineeModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeModuleResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.service.TraineeModuleService;
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

@WebMvcTest(controllers = TraineeModuleController.class)
public class TraineeModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TraineeModuleService traineeModuleService;

    private TraineeModuleResponseDTO sampleResponse;
    private TraineeModuleRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleResponse = TraineeModuleResponseDTO.builder()
                .traineeModuleId(1L)
                .traineeTemplateId(1L)
                .moduleId(1L)
                .moduleName("Core Java")
                .status(ProgressStatus.NOT_STARTED)
                .updatedAt(LocalDateTime.now())
                .completedAt(null)
                .build();

        sampleRequest = new TraineeModuleRequestDTO(1L, 1L, null);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createTraineeModule_whenCreated_returns201() throws Exception {
        when(traineeModuleService.createTraineeModule(any(TraineeModuleRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/trainee-modules")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.traineeModuleId").value(1))
                .andExpect(jsonPath("$.traineeTemplateId").value(1))
                .andExpect(jsonPath("$.moduleId").value(1))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    @Test
    void createTraineeModule_whenNullTraineeTemplateId_returns400() throws Exception {
        TraineeModuleRequestDTO badRequest = new TraineeModuleRequestDTO(null, 1L, null);

        mockMvc.perform(post("/api/v1/trainee-modules")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(traineeModuleService, never()).createTraineeModule(any());
    }

    @Test
    void createTraineeModule_whenNullModuleId_returns400() throws Exception {
        TraineeModuleRequestDTO badRequest = new TraineeModuleRequestDTO(1L, null, null);

        mockMvc.perform(post("/api/v1/trainee-modules")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(traineeModuleService, never()).createTraineeModule(any());
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllTraineeModules_whenSuccessful_returns200() throws Exception {
        when(traineeModuleService.getAllTraineeModules()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/trainee-modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].traineeModuleId").value(1));
    }

    @Test
    void getAllTraineeModules_whenEmptyList_returns200() throws Exception {
        when(traineeModuleService.getAllTraineeModules()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/trainee-modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getTraineeModuleById_whenSuccessful_returns200() throws Exception {
        when(traineeModuleService.getTraineeModuleById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/trainee-modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traineeModuleId").value(1))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    // ──────────────────────────── GET BY TRAINEE TEMPLATE ────────────────────────────

    @Test
    void getTraineeModulesByTraineeTemplate_whenSuccessful_returns200() throws Exception {
        when(traineeModuleService.getTraineeModulesByTraineeTemplate(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/trainee-modules/trainee-template/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].traineeTemplateId").value(1));
    }

    @Test
    void getTraineeModulesByTraineeTemplate_whenEmptyList_returns200() throws Exception {
        when(traineeModuleService.getTraineeModulesByTraineeTemplate(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/trainee-modules/trainee-template/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY STATUS ────────────────────────────

    @Test
    void getTraineeModulesByStatus_whenSuccessful_returns200() throws Exception {
        when(traineeModuleService.getTraineeModulesByStatus(1L, ProgressStatus.NOT_STARTED))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/trainee-modules/trainee-template/1/status")
                        .param("status", "NOT_STARTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("NOT_STARTED"));
    }

    @Test
    void getTraineeModulesByStatus_whenInvalidStatus_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/trainee-modules/trainee-template/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());

        verify(traineeModuleService, never()).getTraineeModulesByStatus(anyLong(), any());
    }

    // ──────────────────────────── UPDATE STATUS ────────────────────────────

    @Test
    void updateTraineeModuleStatus_whenUpdated_returns200() throws Exception {
        TraineeModuleResponseDTO updatedResponse = TraineeModuleResponseDTO.builder()
                .traineeModuleId(1L)
                .traineeTemplateId(1L)
                .moduleId(1L)
                .moduleName("Core Java")
                .status(ProgressStatus.IN_PROGRESS)
                .updatedAt(LocalDateTime.now())
                .build();

        when(traineeModuleService.updateTraineeModuleStatus(1L, ProgressStatus.IN_PROGRESS))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/trainee-modules/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traineeModuleId").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTraineeModuleStatus_whenInvalidStatus_returns400() throws Exception {
        mockMvc.perform(patch("/api/v1/trainee-modules/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());

        verify(traineeModuleService, never()).updateTraineeModuleStatus(anyLong(), any());
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteTraineeModule_whenDeleted_returns200() throws Exception {
        doNothing().when(traineeModuleService).deleteTraineeModule(1L);

        mockMvc.perform(delete("/api/v1/trainee-modules/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee Module deleted successfully"));
    }
}