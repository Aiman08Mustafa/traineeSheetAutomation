package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TraineeModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeModuleResponseDTO;
import com.example.traineeSheetAutomation.entity.Module;
import com.example.traineeSheetAutomation.entity.Topic;
import com.example.traineeSheetAutomation.entity.TraineeModule;
import com.example.traineeSheetAutomation.entity.TraineeTemplate;
import com.example.traineeSheetAutomation.entity.TraineeTopic;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.repository.ModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeTemplateRepository;
import com.example.traineeSheetAutomation.repository.TopicRepository;
import com.example.traineeSheetAutomation.repository.TraineeTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeModuleService {

    private final TraineeModuleRepository traineeModuleRepository;
    private final TraineeTemplateRepository traineeTemplateRepository;
    private final ModuleRepository moduleRepository;
    private final TopicRepository topicRepository;
    private final TraineeTopicRepository traineeTopicRepository;

    public TraineeModuleResponseDTO createTraineeModule(TraineeModuleRequestDTO request) {

        TraineeTemplate traineeTemplate = traineeTemplateRepository.findById(request.getTraineeTemplateId())
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Template not found with ID: " + request.getTraineeTemplateId()));

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException(
                        "Module not found with ID: " + request.getModuleId()));

        if (traineeModuleRepository.existsByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(
                request.getTraineeTemplateId(), request.getModuleId())) {
            throw new RuntimeException(
                    "Trainee Module already exists for trainee template ID: "
                            + request.getTraineeTemplateId() + " and module ID: " + request.getModuleId());
        }

        TraineeModule traineeModule = new TraineeModule();
        traineeModule.setTraineeTemplate(traineeTemplate);
        traineeModule.setModule(module);

        return convertToDTO(traineeModuleRepository.save(traineeModule));
    }

    public List<TraineeModuleResponseDTO> getAllTraineeModules() {

        List<TraineeModuleResponseDTO> traineeModules = traineeModuleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (traineeModules.isEmpty()) {
            throw new RuntimeException("No trainee modules found");
        }

        return traineeModules;
    }

    public TraineeModuleResponseDTO getTraineeModuleById(Long id) {

        TraineeModule traineeModule = traineeModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Module not found with ID: " + id));
        enforceTraineeOwnership(traineeModule.getTraineeTemplate().getTrainee().getUserId());

        return convertToDTO(traineeModule);
    }

    @Transactional
    public List<TraineeModuleResponseDTO> getTraineeModulesByTraineeTemplate(Long traineeTemplateId) {

        TraineeTemplate traineeTemplate = traineeTemplateRepository.findById(traineeTemplateId)
                .orElseThrow(() -> new RuntimeException("Trainee Template not found with ID: " + traineeTemplateId));
        enforceTraineeOwnership(traineeTemplate.getTrainee().getUserId());

        syncTraineeTemplateSnapshot(traineeTemplate);

        List<TraineeModuleResponseDTO> traineeModules = traineeModuleRepository
                .findByTraineeTemplate_TraineeTemplateId(traineeTemplateId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return traineeModules;
    }

    private void syncTraineeTemplateSnapshot(TraineeTemplate traineeTemplate) {
        List<Module> sourceModules = moduleRepository.findByTemplate_TemplateIdOrderBySequenceOrderAsc(
                traineeTemplate.getTemplate().getTemplateId()
        );
        List<TraineeModule> existingTraineeModules = traineeModuleRepository
                .findByTraineeTemplate_TraineeTemplateId(traineeTemplate.getTraineeTemplateId());
        Map<Long, TraineeModule> traineeModuleBySourceModuleId = new HashMap<>();
        for (TraineeModule tm : existingTraineeModules) {
            traineeModuleBySourceModuleId.put(tm.getModule().getModuleId(), tm);
        }

        List<TraineeModule> newTraineeModules = new ArrayList<>();
        for (Module sourceModule : sourceModules) {
            if (!traineeModuleBySourceModuleId.containsKey(sourceModule.getModuleId())) {
                TraineeModule traineeModule = new TraineeModule();
                traineeModule.setTraineeTemplate(traineeTemplate);
                traineeModule.setModule(sourceModule);
                newTraineeModules.add(traineeModule);
            }
        }
        if (!newTraineeModules.isEmpty()) {
            List<TraineeModule> saved = traineeModuleRepository.saveAll(newTraineeModules);
            for (TraineeModule tm : saved) {
                traineeModuleBySourceModuleId.put(tm.getModule().getModuleId(), tm);
            }
        }

        List<TraineeTopic> newTraineeTopics = new ArrayList<>();
        for (Module sourceModule : sourceModules) {
            TraineeModule traineeModule = traineeModuleBySourceModuleId.get(sourceModule.getModuleId());
            if (traineeModule == null) continue;
            List<Topic> sourceTopics = topicRepository.findByModule_ModuleIdOrderBySequenceOrderAsc(sourceModule.getModuleId());
            for (Topic sourceTopic : sourceTopics) {
                boolean exists = traineeTopicRepository.existsByTraineeModule_TraineeModuleIdAndTopic_TopicId(
                        traineeModule.getTraineeModuleId(), sourceTopic.getTopicId()
                );
                if (!exists) {
                    TraineeTopic traineeTopic = new TraineeTopic();
                    traineeTopic.setTraineeModule(traineeModule);
                    traineeTopic.setTopic(sourceTopic);
                    newTraineeTopics.add(traineeTopic);
                }
            }
        }
        if (!newTraineeTopics.isEmpty()) {
            traineeTopicRepository.saveAll(newTraineeTopics);
        }
    }

    public List<TraineeModuleResponseDTO> getTraineeModulesByStatus(Long traineeTemplateId, ProgressStatus status) {

        TraineeTemplate traineeTemplate = traineeTemplateRepository.findById(traineeTemplateId)
                .orElseThrow(() -> new RuntimeException("Trainee Template not found with ID: " + traineeTemplateId));
        enforceTraineeOwnership(traineeTemplate.getTrainee().getUserId());

        List<TraineeModuleResponseDTO> traineeModules = traineeModuleRepository
                .findByTraineeTemplate_TraineeTemplateIdAndStatus(traineeTemplateId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (traineeModules.isEmpty()) {
            throw new RuntimeException("No trainee modules found with status: " + status);
        }

        return traineeModules;
    }

    public TraineeModuleResponseDTO updateTraineeModuleStatus(Long id, ProgressStatus status) {

        TraineeModule traineeModule = traineeModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Module not found with ID: " + id));
        enforceTraineeOwnership(traineeModule.getTraineeTemplate().getTrainee().getUserId());

        traineeModule.setStatus(status);

        return convertToDTO(traineeModuleRepository.save(traineeModule));
    }

    public void deleteTraineeModule(Long id) {

        if (!traineeModuleRepository.existsById(id)) {
            throw new RuntimeException("Trainee Module not found with ID: " + id);
        }

        traineeModuleRepository.deleteById(id);
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
            throw new RuntimeException("You are not allowed to access another trainee's modules.");
        }
    }

    private TraineeModuleResponseDTO convertToDTO(TraineeModule traineeModule) {
        return TraineeModuleResponseDTO.builder()
                .traineeModuleId(traineeModule.getTraineeModuleId())
                .traineeTemplateId(traineeModule.getTraineeTemplate().getTraineeTemplateId())
                .moduleId(traineeModule.getModule().getModuleId())
                .moduleName(traineeModule.getModule().getModuleName())
                .status(traineeModule.getStatus())
                .updatedAt(traineeModule.getUpdatedAt())
                .completedAt(traineeModule.getCompletedAt())
                .build();
    }
}