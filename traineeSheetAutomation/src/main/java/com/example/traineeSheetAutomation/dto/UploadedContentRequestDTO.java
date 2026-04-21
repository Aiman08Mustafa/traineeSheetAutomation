package com.example.traineeSheetAutomation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedContentRequestDTO {

    @NotNull(message = "Trainee Topic ID is required")
    @Positive(message = "Trainee Topic ID must be a positive number")
    private Long traineeTopicId;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File type is required")
    private String fileType;

    @NotBlank(message = "File path is required")
    private String filePath;

    private String additionalNote;
}