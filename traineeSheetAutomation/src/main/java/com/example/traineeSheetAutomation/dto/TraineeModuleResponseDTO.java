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
public class TraineeModuleResponseDTO {

    private Long traineeModuleId;
    private Long traineeTemplateId;
    private Long moduleId;
    private String moduleName;
    private ProgressStatus status;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}