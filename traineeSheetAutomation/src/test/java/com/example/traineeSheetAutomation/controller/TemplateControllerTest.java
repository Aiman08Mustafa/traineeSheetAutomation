package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TemplateResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.Title;
import com.example.traineeSheetAutomation.service.TemplateService;
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

@WebMvcTest(controllers = TemplateController.class)
public class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TemplateService templateService;

    private TemplateResponseDTO sampleResponse;
    private TemplateRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleResponse = TemplateResponseDTO.builder()
                .templateId(1L)
                .title(Title.JAVA)
                .description("Sample Java template description")
                .createdBy(1L)
                .createdByName("Aiman")
                .serviceLineId(1L)
                .serviceLineDepartment("DEVELOPMENT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = TemplateRequestDTO.builder()
                .title(Title.JAVA)
                .description("Sample Java template description")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();
    }

    // ───── POST /api/v1/templates ─────

    @Test
    void createTemplate_whenCreated_returns201() throws Exception {
        when(templateService.createTemplate(any(TemplateRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.templateId").value(1))
                .andExpect(jsonPath("$.title").value("JAVA"))
                .andExpect(jsonPath("$.description").value("Sample Java template description"))
                .andExpect(jsonPath("$.createdBy").value(1))
                .andExpect(jsonPath("$.createdByName").value("Aiman"))
                .andExpect(jsonPath("$.serviceLineId").value(1))
                .andExpect(jsonPath("$.serviceLineDepartment").value("DEVELOPMENT"));
    }

    @Test
    void createTemplate_whenNullTitle_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(null)
                .description("Some description")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).createTemplate(any());
    }

    @Test
    void createTemplate_whenInvalidTitle_returns400() throws Exception {
        String invalidBody = """
                {
                  "title": "INVALID_TITLE",
                  "description": "Some description",
                  "createdBy": 1,
                  "serviceLineId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidBody))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).createTemplate(any());
    }

    @Test
    void createTemplate_whenBlankDescription_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(Title.JAVA)
                .description("")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).createTemplate(any());
    }

    @Test
    void createTemplate_whenNullCreatedBy_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(Title.JAVA)
                .description("Some description")
                .createdBy(null)
                .serviceLineId(1L)
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).createTemplate(any());
    }

    @Test
    void createTemplate_whenNegativeCreatedBy_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(Title.JAVA)
                .description("Some description")
                .createdBy(-1L)
                .serviceLineId(1L)
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).createTemplate(any());
    }

    @Test
    void createTemplate_whenNullServiceLineId_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(Title.JAVA)
                .description("Some description")
                .createdBy(1L)
                .serviceLineId(null)
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).createTemplate(any());
    }

    @Test
    void createTemplate_whenNegativeServiceLineId_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(Title.JAVA)
                .description("Some description")
                .createdBy(1L)
                .serviceLineId(-5L)
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).createTemplate(any());
    }

    // ───── GET /api/v1/templates ─────

    @Test
    void getAllTemplates_whenSuccessful_returns200() throws Exception {
        when(templateService.getAllTemplates()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("JAVA"))
                .andExpect(jsonPath("$[0].createdByName").value("Aiman"))
                .andExpect(jsonPath("$[0].serviceLineDepartment").value("DEVELOPMENT"));
    }

    @Test
    void getAllTemplates_whenEmptyList_returns200() throws Exception {
        when(templateService.getAllTemplates()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ───── GET /api/v1/templates/{id} ─────

    @Test
    void getTemplateById_whenSuccessful_returns200() throws Exception {
        when(templateService.getTemplateById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/templates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templateId").value(1))
                .andExpect(jsonPath("$.title").value("JAVA"))
                .andExpect(jsonPath("$.createdByName").value("Aiman"))
                .andExpect(jsonPath("$.serviceLineDepartment").value("DEVELOPMENT"));
    }

    // ───── GET /api/v1/templates/user/{userId} ─────

    @Test
    void getTemplatesByUser_whenSuccessful_returns200() throws Exception {
        when(templateService.getTemplatesByUser(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/templates/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].templateId").value(1))
                .andExpect(jsonPath("$[0].title").value("JAVA"))
                .andExpect(jsonPath("$[0].createdBy").value(1))
                .andExpect(jsonPath("$[0].createdByName").value("Aiman"));
    }

    @Test
    void getTemplatesByUser_whenNoTemplates_returns200() throws Exception {
        when(templateService.getTemplatesByUser(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/templates/user/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ───── PUT /api/v1/templates/{id} ─────

    @Test
    void updateTemplate_whenUpdated_returns200() throws Exception {
        when(templateService.updateTemplate(eq(1L), any(TemplateRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/templates/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templateId").value(1))
                .andExpect(jsonPath("$.title").value("JAVA"))
                .andExpect(jsonPath("$.description").value("Sample Java template description"))
                .andExpect(jsonPath("$.createdByName").value("Aiman"))
                .andExpect(jsonPath("$.serviceLineDepartment").value("DEVELOPMENT"));
    }

    @Test
    void updateTemplate_whenNullTitle_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(null)
                .description("Some description")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();

        mockMvc.perform(put("/api/v1/templates/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).updateTemplate(anyLong(), any());
    }

    @Test
    void updateTemplate_whenBlankDescription_returns400() throws Exception {
        TemplateRequestDTO badRequest = TemplateRequestDTO.builder()
                .title(Title.REACT)
                .description("")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();

        mockMvc.perform(put("/api/v1/templates/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).updateTemplate(anyLong(), any());
    }

    @Test
    void updateTemplate_whenInvalidTitle_returns400() throws Exception {
        String invalidBody = """
                {
                  "title": "INVALID_TITLE",
                  "description": "Some description",
                  "createdBy": 1,
                  "serviceLineId": 1
                }
                """;

        mockMvc.perform(put("/api/v1/templates/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidBody))
                .andExpect(status().isBadRequest());

        verify(templateService, never()).updateTemplate(anyLong(), any());
    }

    // ───── DELETE /api/v1/templates/{id} ─────

    @Test
    void deleteTemplate_whenDeleted_returns200() throws Exception {
        doNothing().when(templateService).deleteTemplate(1L);

        mockMvc.perform(delete("/api/v1/templates/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Template deleted successfully"));
    }
}