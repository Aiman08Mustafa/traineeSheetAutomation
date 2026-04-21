package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TopicRequestDTO;
import com.example.traineeSheetAutomation.dto.TopicResponseDTO;
import com.example.traineeSheetAutomation.service.TopicService;
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

@WebMvcTest(controllers = TopicController.class)
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TopicService topicService;

    private TopicResponseDTO sampleResponse;
    private TopicRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleResponse = TopicResponseDTO.builder()
                .topicId(1L)
                .moduleId(1L)
                .moduleName("Core Java")
                .topicName("OOP Concepts")
                .learningObjectives("Understand OOP")
                .readingMaterial("Chapter 1")
                .assignment("Assignment 1")
                .sequenceOrder(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = new TopicRequestDTO(
                1L,
                "OOP Concepts",
                "Understand OOP",
                "Chapter 1",
                "Assignment 1",
                1
        );
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createTopic_whenCreated_returns201() throws Exception {
        when(topicService.createTopic(any(TopicRequestDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.topicId").value(1))
                .andExpect(jsonPath("$.topicName").value("OOP Concepts"))
                .andExpect(jsonPath("$.moduleId").value(1));
    }

    @Test
    void createTopic_whenNullModuleId_returns400() throws Exception {
        TopicRequestDTO badRequest = new TopicRequestDTO(null, "OOP Concepts", "Understand OOP", "Chapter 1", "Assignment 1", 1);

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(topicService, never()).createTopic(any());
    }

    @Test
    void createTopic_whenNullTopicName_returns400() throws Exception {
        TopicRequestDTO badRequest = new TopicRequestDTO(1L, null, "Understand OOP", "Chapter 1", "Assignment 1", 1);

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(topicService, never()).createTopic(any());
    }

    @Test
    void createTopic_whenNullSequenceOrder_returns400() throws Exception {
        TopicRequestDTO badRequest = new TopicRequestDTO(1L, "OOP Concepts", "Understand OOP", "Chapter 1", "Assignment 1", null);

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(topicService, never()).createTopic(any());
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllTopics_whenSuccessful_returns200() throws Exception {
        when(topicService.getAllTopics()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].topicName").value("OOP Concepts"));
    }

    @Test
    void getAllTopics_whenEmptyList_returns200() throws Exception {
        when(topicService.getAllTopics()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getTopicById_whenSuccessful_returns200() throws Exception {
        when(topicService.getTopicById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicId").value(1))
                .andExpect(jsonPath("$.topicName").value("OOP Concepts"));
    }

    // ──────────────────────────── GET BY MODULE ────────────────────────────

    @Test
    void getTopicByModule_whenSuccessful_returns200() throws Exception {
        when(topicService.getTopicByModule(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/topics/module/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].moduleId").value(1));
    }

    @Test
    void getTopicByModule_whenEmptyList_returns200() throws Exception {
        when(topicService.getTopicByModule(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/topics/module/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    @Test
    void updateTopic_whenUpdated_returns200() throws Exception {
        when(topicService.updateTopic(eq(1L), any(TopicRequestDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/topics/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicId").value(1))
                .andExpect(jsonPath("$.topicName").value("OOP Concepts"));
    }

    @Test
    void updateTopic_whenNullModuleId_returns400() throws Exception {
        TopicRequestDTO badRequest = new TopicRequestDTO(null, "OOP Concepts", "Understand OOP", "Chapter 1", "Assignment 1", 1);

        mockMvc.perform(put("/api/v1/topics/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(topicService, never()).updateTopic(anyLong(), any());
    }

    @Test
    void updateTopic_whenNullTopicName_returns400() throws Exception {
        TopicRequestDTO badRequest = new TopicRequestDTO(1L, null, "Understand OOP", "Chapter 1", "Assignment 1", 1);

        mockMvc.perform(put("/api/v1/topics/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(topicService, never()).updateTopic(anyLong(), any());
    }

    @Test
    void updateTopic_whenNullSequenceOrder_returns400() throws Exception {
        TopicRequestDTO badRequest = new TopicRequestDTO(1L, "OOP Concepts", "Understand OOP", "Chapter 1", "Assignment 1", null);

        mockMvc.perform(put("/api/v1/topics/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(topicService, never()).updateTopic(anyLong(), any());
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteTopic_whenDeleted_returns200() throws Exception {
        doNothing().when(topicService).deleteTopic(1L);

        mockMvc.perform(delete("/api/v1/topics/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Topic deleted successfully"));
    }
}