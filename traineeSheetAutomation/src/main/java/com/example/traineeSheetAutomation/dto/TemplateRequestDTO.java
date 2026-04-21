package com.example.traineeSheetAutomation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Created By (User ID) is required")
    @Positive(message = "User ID must be a positive number")
    private Long createdBy;

    @NotNull(message = "Service Line ID is required")
    @Positive(message = "Service Line ID must be a positive number")
    private Long serviceLineId;

}
