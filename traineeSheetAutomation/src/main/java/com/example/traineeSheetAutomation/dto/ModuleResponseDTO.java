package com.example.traineeSheetAutomation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponseDTO {
    private Long moduleId;
    private Long templateId;
    private String templateTitle;
    private String moduleName;
    private String description;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
