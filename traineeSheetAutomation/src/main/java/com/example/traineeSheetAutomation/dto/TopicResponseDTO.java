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
public class TopicResponseDTO {
    private Long topicId;
    private Long moduleId;
    private String moduleName;
    private String topicName;
    private String learningObjectives;
    private String readingMaterial;
    private String assignment;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
