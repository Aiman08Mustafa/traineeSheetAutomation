package com.example.traineeSheetAutomation.dto;

import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTopicResponseDTO {

    private Long traineeTopicId;
    private Long traineeModuleId;
    private Long topicId;
    private String topicName;
    private String learningObjective;
    private String readingMaterial;
    private String assignment;
    private Integer sequenceOrder;
    private ProgressStatus status;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}