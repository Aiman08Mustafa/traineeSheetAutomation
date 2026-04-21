package com.example.traineeSheetAutomation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTemplateRequestDTO {

    @NotNull(message = "Template ID is required")
    @Positive(message = "Template ID must be a positive number")
    private Long templateId;

    @NotNull(message = "Trainee ID is required")
    @Positive(message = "Trainee ID must be a positive number")
    private Long traineeId;
}