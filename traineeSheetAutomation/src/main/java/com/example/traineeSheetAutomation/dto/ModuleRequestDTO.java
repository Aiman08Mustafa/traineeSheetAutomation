package com.example.traineeSheetAutomation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRequestDTO {

    @NotNull(message = "Template ID is required")
    @Positive(message = "Template ID must be a positive number")
    private Long templateId;

    @NotBlank(message = "Module name is required")
    @Size(min = 2, max = 100, message = "Module name must be between 2 and 100 characters")
    private String moduleName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Sequence order is required")
    @Positive(message = "Sequence order must be a positive number")
    private Integer sequenceOrder;
}