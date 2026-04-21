package com.example.traineeSheetAutomation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedContentResponseDTO {

    private Long contentId;
    private Long traineeTopicId;
    private String topicName;
    private String fileName;
    private String fileType;
    private String filePath;
    private String additionalNote;
    private LocalDateTime uploadedAt;
}