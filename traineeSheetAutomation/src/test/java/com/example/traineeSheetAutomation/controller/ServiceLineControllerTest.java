package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.ServiceLineRequestDTO;
import com.example.traineeSheetAutomation.dto.ServiceLineResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.Department;
import com.example.traineeSheetAutomation.service.ServiceLineService;
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

@WebMvcTest(controllers = ServiceLineController.class)
public class ServiceLineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ServiceLineService serviceLineService;

    private ServiceLineResponseDTO sampleResponse;
    private ServiceLineRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleResponse = new ServiceLineResponseDTO(
                1L,
                Department.DEVELOPMENT
        );

        sampleRequest = new ServiceLineRequestDTO(
                Department.DEVELOPMENT
        );
    }

    @Test
    void createServiceLine_whenCreated_returns201() throws Exception {
        when(serviceLineService.createServiceLine(any(ServiceLineRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/service-lines")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceLineId").value(1))
                .andExpect(jsonPath("$.department").value("DEVELOPMENT"));
    }

    @Test
    void createServiceLine_whenNullDepartment_returns400() throws Exception {
        ServiceLineRequestDTO badRequest = new ServiceLineRequestDTO(null);

        mockMvc.perform(post("/api/v1/service-lines")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(serviceLineService, never()).createServiceLine(any());
    }

    @Test
    void createServiceLine_whenInvalidDepartment_returns400() throws Exception {
        String invalidBody = "{\"department\": \"INVALID_DEPARTMENT\"}";

        mockMvc.perform(post("/api/v1/service-lines")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidBody))
                .andExpect(status().isBadRequest());

        verify(serviceLineService, never()).createServiceLine(any());
    }

    @Test
    void getAllServiceLines_whenSuccessful_returns200() throws Exception {
        when(serviceLineService.getAllServiceLines()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/service-lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].department").value("DEVELOPMENT"));
    }

    @Test
    void getAllServiceLines_whenEmptyList_returns200() throws Exception {
        when(serviceLineService.getAllServiceLines()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/service-lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getServiceLineById_whenSuccessful_returns200() throws Exception {
        when(serviceLineService.getServiceLineById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/service-lines/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceLineId").value(1))
                .andExpect(jsonPath("$.department").value("DEVELOPMENT"));
    }

    @Test
    void updateServiceLine_whenUpdated_returns200() throws Exception {
        when(serviceLineService.updateServiceLine(eq(1L), any(ServiceLineRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/service-lines/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("DEVELOPMENT"));
    }

    @Test
    void updateServiceLine_whenNullDepartment_returns400() throws Exception {
        ServiceLineRequestDTO badRequest = new ServiceLineRequestDTO(null);

        mockMvc.perform(put("/api/v1/service-lines/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(serviceLineService, never()).updateServiceLine(anyLong(), any());
    }

    @Test
    void updateServiceLine_whenInvalidDepartment_returns400() throws Exception {
        String invalidBody = "{\"department\": \"INVALID_DEPARTMENT\"}";

        mockMvc.perform(put("/api/v1/service-lines/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidBody))
                .andExpect(status().isBadRequest());

        verify(serviceLineService, never()).updateServiceLine(anyLong(), any());
    }

    @Test
    void deleteServiceLine_whenDeleted_returns200() throws Exception {
        doNothing().when(serviceLineService).deleteServiceLine(1L);

        mockMvc.perform(delete("/api/v1/service-lines/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service Line deleted successfully"));
    }
}