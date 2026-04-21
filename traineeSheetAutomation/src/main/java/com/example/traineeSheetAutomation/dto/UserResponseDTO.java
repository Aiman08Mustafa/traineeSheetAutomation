package com.example.traineeSheetAutomation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long userId;
    private String name;
    private String email;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
