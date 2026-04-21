package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.UserRequestDTO;
import com.example.traineeSheetAutomation.dto.UserResponseDTO;
import com.example.traineeSheetAutomation.service.UserService;
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
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    private UserResponseDTO sampleResponse;
    private UserRequestDTO sampleRequest;

    @BeforeEach
    void setup(){
        sampleResponse = new UserResponseDTO(
                1L,
                "Aiman",
                "abc@example.com",
                "TRAINEE",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        sampleRequest = new UserRequestDTO(
                "Aiman",
                "abc@example.com",
                "123secret",
                1L
        );
    }

    @Test
    void createUser_whenCreated_returns201() throws Exception{
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Aiman"))
                .andExpect(jsonPath("$.email").value("abc@example.com"))
                .andExpect(jsonPath("$.roleName").value("TRAINEE"));
    }

    @Test
    void createUser_whenBlankName_returns400() throws Exception{
        UserRequestDTO badRequest = new UserRequestDTO("", "abc@example.com", "123secret", 1L);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_whenInvalidEmail_returns400() throws Exception{
        UserRequestDTO badRequest = new UserRequestDTO("Aiman", "not-an-email", "123secret", 1L);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_whenShortPassword_returns400() throws Exception {
        UserRequestDTO badRequest = new UserRequestDTO("Aiman", "abc@example.com", "123", 1L);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenNullRoleId_returns400() throws Exception {
        UserRequestDTO badRequest = new UserRequestDTO("Aiman", "abc@example.com", "123secret", null);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_whenSuccessful_returns200() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Aiman"));
    }

    @Test
    void getAllUsers_whenEmptyList_returns200() throws Exception {

        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUserById_whenSuccessful_returns200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("abc@example.com"));
    }

    @Test
    void updateUser_whenUpdated_returns200() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aiman"));
    }

    @Test
    void updateUser_whenInvalidBody_returns400() throws Exception {
        UserRequestDTO bad = new UserRequestDTO("", "bad-email", "x", null);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any());
    }

    @Test
    void deleteUser_whenDeleted_returns200() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

}
