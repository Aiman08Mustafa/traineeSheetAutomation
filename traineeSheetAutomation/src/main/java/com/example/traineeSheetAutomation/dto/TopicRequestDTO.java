package com.example.traineeSheetAutomation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicRequestDTO {

    @NotNull(message = "Module ID is required")
    @Positive(message = "Module ID must be a poositive number")
    private Long moduleId;

    @NotBlank(message = "Topic name is required")
    @Size(min = 2, max = 100, message = "Topic name must be between 2 and 100 characters")
    private String topicName;

    @NotBlank(message = "Learning Objectives are required")
    private String learningObjective;

    @NotBlank(message = "Reading Materail is required")
    private String readingMaterial;

    private String assignment;

    @NotNull(message = "Sequence order is required")
    @Positive(message = "Sequence order must be a positive number")
    private Integer sequenceOrder;
}
