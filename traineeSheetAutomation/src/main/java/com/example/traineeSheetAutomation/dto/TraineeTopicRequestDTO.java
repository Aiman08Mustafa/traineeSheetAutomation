package com.example.traineeSheetAutomation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTopicRequestDTO {

    @NotNull(message = "Trainee Module ID is required")
    @Positive(message = "Trainee Module ID must be a positive number")
    private Long traineeModuleId;

    @NotNull(message = "Topic ID is required")
    @Positive(message = "Topic ID must be a positive number")
    private Long topicId;
}