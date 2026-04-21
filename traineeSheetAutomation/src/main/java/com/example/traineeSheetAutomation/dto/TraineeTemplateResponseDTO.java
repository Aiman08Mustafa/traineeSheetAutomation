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
public class TraineeTemplateResponseDTO {

    private Long traineeTemplateId;
    private Long templateId;
    private String templateTitle;
    private Long traineeId;
    private String traineeName;
    private ProgressStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime updatedAt;
}