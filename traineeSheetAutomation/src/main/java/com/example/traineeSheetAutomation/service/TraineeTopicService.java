package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TraineeTopicRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeTopicResponseDTO;
import com.example.traineeSheetAutomation.entity.Topic;
import com.example.traineeSheetAutomation.entity.TraineeModule;
import com.example.traineeSheetAutomation.entity.TraineeTopic;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.repository.TopicRepository;
import com.example.traineeSheetAutomation.repository.TraineeModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeTopicService {

    private final TraineeTopicRepository traineeTopicRepository;
    private final TraineeModuleRepository traineeModuleRepository;
    private final TopicRepository topicRepository;

    public TraineeTopicResponseDTO createTraineeTopic(TraineeTopicRequestDTO request) {

        TraineeModule traineeModule = traineeModuleRepository.findById(request.getTraineeModuleId())
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Module not found with ID: " + request.getTraineeModuleId()));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException(
                        "Topic not found with ID: " + request.getTopicId()));

        if (traineeTopicRepository.existsByTraineeModule_TraineeModuleIdAndTopic_TopicId(
                request.getTraineeModuleId(), request.getTopicId())) {
            throw new RuntimeException(
                    "Topic ID: " + request.getTopicId()
                            + " is already assigned to trainee module ID: " + request.getTraineeModuleId());
        }

        TraineeTopic traineeTopic = new TraineeTopic();
        traineeTopic.setTraineeModule(traineeModule);
        traineeTopic.setTopic(topic);

        return convertToDTO(traineeTopicRepository.save(traineeTopic));
    }

    public List<TraineeTopicResponseDTO> getAllTraineeTopics() {

        List<TraineeTopicResponseDTO> traineeTopics = traineeTopicRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (traineeTopics.isEmpty()) {
            throw new RuntimeException("No trainee topics found");
        }

        return traineeTopics;
    }

    public TraineeTopicResponseDTO getTraineeTopicById(Long id) {

        TraineeTopic traineeTopic = traineeTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Topic not found with ID: " + id));
        enforceTraineeOwnership(traineeTopic.getTraineeModule().getTraineeTemplate().getTrainee().getUserId());

        return convertToDTO(traineeTopic);
    }

    public List<TraineeTopicResponseDTO> getTraineeTopicsByTraineeModule(Long traineeModuleId) {

        TraineeModule traineeModule = traineeModuleRepository.findById(traineeModuleId)
                .orElseThrow(() -> new RuntimeException("Trainee Module not found with ID: " + traineeModuleId));
        enforceTraineeOwnership(traineeModule.getTraineeTemplate().getTrainee().getUserId());

        List<TraineeTopicResponseDTO> traineeTopics = traineeTopicRepository
                .findByTraineeModule_TraineeModuleId(traineeModuleId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return traineeTopics;
    }

    public List<TraineeTopicResponseDTO> getTraineeTopicsByStatus(Long traineeModuleId, ProgressStatus status) {

        TraineeModule traineeModule = traineeModuleRepository.findById(traineeModuleId)
                .orElseThrow(() -> new RuntimeException("Trainee Module not found with ID: " + traineeModuleId));
        enforceTraineeOwnership(traineeModule.getTraineeTemplate().getTrainee().getUserId());

        List<TraineeTopicResponseDTO> traineeTopics = traineeTopicRepository
                .findByTraineeModule_TraineeModuleIdAndStatus(traineeModuleId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (traineeTopics.isEmpty()) {
            throw new RuntimeException("No trainee topics found with status: " + status);
        }

        return traineeTopics;
    }

    public TraineeTopicResponseDTO updateTraineeTopicStatus(Long id, ProgressStatus status) {

        TraineeTopic traineeTopic = traineeTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Topic not found with ID: " + id));
        enforceTraineeOwnership(traineeTopic.getTraineeModule().getTraineeTemplate().getTrainee().getUserId());

        traineeTopic.setStatus(status);

        return convertToDTO(traineeTopicRepository.save(traineeTopic));
    }

    public void deleteTraineeTopic(Long id) {

        if (!traineeTopicRepository.existsById(id)) {
            throw new RuntimeException("Trainee Topic not found with ID: " + id);
        }

        traineeTopicRepository.deleteById(id);
    }

    private void enforceTraineeOwnership(Long traineeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return;
        }

        boolean isTrainee = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_TRAINEE".equals(authority.getAuthority()));
        if (!isTrainee) {
            return;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user && !user.getUserId().equals(traineeId)) {
            throw new RuntimeException("You are not allowed to access another trainee's topics.");
        }
    }

    private TraineeTopicResponseDTO convertToDTO(TraineeTopic traineeTopic) {
        return TraineeTopicResponseDTO.builder()
                .traineeTopicId(traineeTopic.getTraineeTopicId())
                .traineeModuleId(traineeTopic.getTraineeModule().getTraineeModuleId())
                .topicId(traineeTopic.getTopic().getTopicId())
                .topicName(traineeTopic.getTopic().getTopicName())
                .learningObjective(traineeTopic.getTopic().getLearningObjective())
                .readingMaterial(traineeTopic.getTopic().getReadingMaterial())
                .assignment(traineeTopic.getTopic().getAssignment())
                .sequenceOrder(traineeTopic.getTopic().getSequenceOrder())
                .status(traineeTopic.getStatus())
                .updatedAt(traineeTopic.getUpdatedAt())
                .completedAt(traineeTopic.getCompletedAt())
                .build();
    }
}