package com.example.traineeSheetAutomation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {
    private Long userId;
    private String name;
    private String token;
    private String email;
    private String role;
}