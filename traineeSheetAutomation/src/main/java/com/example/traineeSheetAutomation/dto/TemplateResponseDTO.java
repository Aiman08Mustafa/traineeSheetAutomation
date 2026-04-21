package com.example.traineeSheetAutomation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponseDTO {
    private Long templateId;
    private String title;
    private String description;
    private Long createdBy;
    private String createdByName;
    private Long serviceLineId;
    private String serviceLineDepartment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
