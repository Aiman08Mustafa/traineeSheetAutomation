package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.UploadedContentRequestDTO;
import com.example.traineeSheetAutomation.dto.UploadedContentResponseDTO;
import com.example.traineeSheetAutomation.entity.TraineeTopic;
import com.example.traineeSheetAutomation.entity.UploadedContent;
import com.example.traineeSheetAutomation.repository.TraineeTopicRepository;
import com.example.traineeSheetAutomation.repository.UploadedContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UploadedContentService {

    private final UploadedContentRepository uploadedContentRepository;
    private final TraineeTopicRepository traineeTopicRepository;

    public UploadedContentResponseDTO uploadContent(UploadedContentRequestDTO request) {

        TraineeTopic traineeTopic = traineeTopicRepository.findById(request.getTraineeTopicId())
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Topic not found with ID: " + request.getTraineeTopicId()));

        if (uploadedContentRepository.existsByTraineeTopic_TraineeTopicIdAndFileName(
                request.getTraineeTopicId(), request.getFileName())) {
            throw new RuntimeException(
                    "File '" + request.getFileName()
                            + "' already uploaded for trainee topic ID: " + request.getTraineeTopicId());
        }

        UploadedContent uploadedContent = new UploadedContent();
        uploadedContent.setTraineeTopic(traineeTopic);
        uploadedContent.setFileName(request.getFileName());
        uploadedContent.setFileType(request.getFileType());
        uploadedContent.setFilePath(request.getFilePath());
        uploadedContent.setAdditionalNote(request.getAdditionalNote());

        return convertToDTO(uploadedContentRepository.save(uploadedContent));
    }

    public List<UploadedContentResponseDTO> getAllUploadedContents() {

        List<UploadedContentResponseDTO> contents = uploadedContentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (contents.isEmpty()) {
            throw new RuntimeException("No uploaded contents found");
        }

        return contents;
    }

    public UploadedContentResponseDTO getUploadedContentById(Long id) {

        UploadedContent uploadedContent = uploadedContentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Uploaded Content not found with ID: " + id));

        return convertToDTO(uploadedContent);
    }

    public List<UploadedContentResponseDTO> getUploadedContentsByTraineeTopic(Long traineeTopicId) {

        if (!traineeTopicRepository.existsById(traineeTopicId)) {
            throw new RuntimeException("Trainee Topic not found with ID: " + traineeTopicId);
        }

        List<UploadedContentResponseDTO> contents = uploadedContentRepository
                .findByTraineeTopic_TraineeTopicId(traineeTopicId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (contents.isEmpty()) {
            throw new RuntimeException("No uploaded contents found for trainee topic ID: " + traineeTopicId);
        }

        return contents;
    }

    public UploadedContentResponseDTO updateUploadedContent(Long id, UploadedContentRequestDTO request) {

        UploadedContent uploadedContent = uploadedContentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Uploaded Content not found with ID: " + id));

        TraineeTopic traineeTopic = traineeTopicRepository.findById(request.getTraineeTopicId())
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Topic not found with ID: " + request.getTraineeTopicId()));

        // Check duplicate file name only if file name actually changed
        boolean fileNameChanged = !uploadedContent.getFileName().equals(request.getFileName());
        boolean topicChanged = !uploadedContent.getTraineeTopic().getTraineeTopicId()
                .equals(request.getTraineeTopicId());

        if ((fileNameChanged || topicChanged) &&
                uploadedContentRepository.existsByTraineeTopic_TraineeTopicIdAndFileName(
                        request.getTraineeTopicId(), request.getFileName())) {
            throw new RuntimeException(
                    "File '" + request.getFileName()
                            + "' already uploaded for trainee topic ID: " + request.getTraineeTopicId());
        }

        uploadedContent.setTraineeTopic(traineeTopic);
        uploadedContent.setFileName(request.getFileName());
        uploadedContent.setFileType(request.getFileType());
        uploadedContent.setFilePath(request.getFilePath());
        uploadedContent.setAdditionalNote(request.getAdditionalNote());

        return convertToDTO(uploadedContentRepository.save(uploadedContent));
    }

    public void deleteUploadedContent(Long id) {

        if (!uploadedContentRepository.existsById(id)) {
            throw new RuntimeException("Uploaded Content not found with ID: " + id);
        }

        uploadedContentRepository.deleteById(id);
    }

    private UploadedContentResponseDTO convertToDTO(UploadedContent uploadedContent) {
        return UploadedContentResponseDTO.builder()
                .contentId(uploadedContent.getContentId())
                .traineeTopicId(uploadedContent.getTraineeTopic().getTraineeTopicId())
                .topicName(uploadedContent.getTraineeTopic().getTopic().getTopicName())
                .fileName(uploadedContent.getFileName())
                .fileType(uploadedContent.getFileType())
                .filePath(uploadedContent.getFilePath())
                .additionalNote(uploadedContent.getAdditionalNote())
                .uploadedAt(uploadedContent.getUploadedAt())
                .build();
    }
}