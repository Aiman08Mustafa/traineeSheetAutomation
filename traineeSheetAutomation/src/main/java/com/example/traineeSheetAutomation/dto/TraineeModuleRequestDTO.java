package com.example.traineeSheetAutomation.dto;

import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeModuleRequestDTO {

    @NotNull(message = "Trainee Template ID is required")
    @Positive(message = "Trainee Template ID must be a positive number")
    private Long traineeTemplateId;

    @NotNull(message = "Module ID is required")
    @Positive(message = "Module ID must be a positive number")
    private Long moduleId;

    private ProgressStatus status;


}