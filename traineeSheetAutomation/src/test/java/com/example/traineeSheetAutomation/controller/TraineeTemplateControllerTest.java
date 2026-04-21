package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TraineeTemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeTemplateResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.service.TraineeTemplateService;
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

@WebMvcTest(controllers = TraineeTemplateController.class)
public class TraineeTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TraineeTemplateService traineeTemplateService;

    private TraineeTemplateResponseDTO sampleResponse;
    private TraineeTemplateRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleResponse = TraineeTemplateResponseDTO.builder()
                .traineeTemplateId(1L)
                .templateId(1L)
                .templateTitle("JAVA")
                .traineeId(1L)
                .traineeName("John Doe")
                .status(ProgressStatus.NOT_STARTED)
                .startedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = new TraineeTemplateRequestDTO(1L, 1L);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createTraineeTemplate_whenCreated_returns201() throws Exception {
        when(traineeTemplateService.createTraineeTemplate(any(TraineeTemplateRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/trainee-templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.traineeTemplateId").value(1))
                .andExpect(jsonPath("$.templateId").value(1))
                .andExpect(jsonPath("$.traineeId").value(1))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    @Test
    void createTraineeTemplate_whenNullTemplateId_returns400() throws Exception {
        TraineeTemplateRequestDTO badRequest = new TraineeTemplateRequestDTO(null, 1L);

        mockMvc.perform(post("/api/v1/trainee-templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(traineeTemplateService, never()).createTraineeTemplate(any());
    }

    @Test
    void createTraineeTemplate_whenNullTraineeId_returns400() throws Exception {
        TraineeTemplateRequestDTO badRequest = new TraineeTemplateRequestDTO(1L, null);

        mockMvc.perform(post("/api/v1/trainee-templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(traineeTemplateService, never()).createTraineeTemplate(any());
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllTraineeTemplates_whenSuccessful_returns200() throws Exception {
        when(traineeTemplateService.getAllTraineeTemplates()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/trainee-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].traineeTemplateId").value(1));
    }

    @Test
    void getAllTraineeTemplates_whenEmptyList_returns200() throws Exception {
        when(traineeTemplateService.getAllTraineeTemplates()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/trainee-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getTraineeTemplateById_whenSuccessful_returns200() throws Exception {
        when(traineeTemplateService.getTraineeTemplateById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/trainee-templates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traineeTemplateId").value(1))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    // ──────────────────────────── GET BY TRAINEE ────────────────────────────

    @Test
    void getTraineeTemplatesByTrainee_whenSuccessful_returns200() throws Exception {
        when(traineeTemplateService.getTraineeTemplatesByTrainee(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/trainee-templates/trainee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].traineeId").value(1));
    }

    @Test
    void getTraineeTemplatesByTrainee_whenEmptyList_returns200() throws Exception {
        when(traineeTemplateService.getTraineeTemplatesByTrainee(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/trainee-templates/trainee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY TEMPLATE ────────────────────────────

    @Test
    void getTraineeTemplatesByTemplate_whenSuccessful_returns200() throws Exception {
        when(traineeTemplateService.getTraineeTemplatesByTemplate(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/trainee-templates/template/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].templateId").value(1));
    }

    @Test
    void getTraineeTemplatesByTemplate_whenEmptyList_returns200() throws Exception {
        when(traineeTemplateService.getTraineeTemplatesByTemplate(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/trainee-templates/template/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY STATUS ────────────────────────────

    @Test
    void getTraineeTemplatesByStatus_whenSuccessful_returns200() throws Exception {
        when(traineeTemplateService.getTraineeTemplatesByStatus(1L, ProgressStatus.NOT_STARTED))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/trainee-templates/trainee/1/status")
                        .param("status", "NOT_STARTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("NOT_STARTED"));
    }

    @Test
    void getTraineeTemplatesByStatus_whenInvalidStatus_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/trainee-templates/trainee/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());

        verify(traineeTemplateService, never()).getTraineeTemplatesByStatus(anyLong(), any());
    }

    // ──────────────────────────── UPDATE STATUS ────────────────────────────

    @Test
    void updateTraineeTemplateStatus_whenUpdated_returns200() throws Exception {
        TraineeTemplateResponseDTO updatedResponse = TraineeTemplateResponseDTO.builder()
                .traineeTemplateId(1L)
                .templateId(1L)
                .templateTitle("JAVA")
                .traineeId(1L)
                .traineeName("John Doe")
                .status(ProgressStatus.IN_PROGRESS)
                .updatedAt(LocalDateTime.now())
                .build();

        when(traineeTemplateService.updateTraineeTemplateStatus(1L, ProgressStatus.IN_PROGRESS))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/trainee-templates/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traineeTemplateId").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTraineeTemplateStatus_whenInvalidStatus_returns400() throws Exception {
        mockMvc.perform(patch("/api/v1/trainee-templates/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());

        verify(traineeTemplateService, never()).updateTraineeTemplateStatus(anyLong(), any());
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteTraineeTemplate_whenDeleted_returns200() throws Exception {
        doNothing().when(traineeTemplateService).deleteTraineeTemplate(1L);

        mockMvc.perform(delete("/api/v1/trainee-templates/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee Template deleted successfully"));
    }
}