package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TraineeTemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeTemplateResponseDTO;
import com.example.traineeSheetAutomation.entity.Module;
import com.example.traineeSheetAutomation.entity.Template;
import com.example.traineeSheetAutomation.entity.Topic;
import com.example.traineeSheetAutomation.entity.TraineeModule;
import com.example.traineeSheetAutomation.entity.TraineeTemplate;
import com.example.traineeSheetAutomation.entity.TraineeTopic;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.repository.ModuleRepository;
import com.example.traineeSheetAutomation.repository.TemplateRepository;
import com.example.traineeSheetAutomation.repository.TopicRepository;
import com.example.traineeSheetAutomation.repository.TraineeModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeTemplateRepository;
import com.example.traineeSheetAutomation.repository.TraineeTopicRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeTemplateService {

    private final TraineeTemplateRepository traineeTemplateRepository;
    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final TopicRepository topicRepository;
    private final TraineeModuleRepository traineeModuleRepository;
    private final TraineeTopicRepository traineeTopicRepository;

    @Transactional
    public TraineeTemplateResponseDTO createTraineeTemplate(TraineeTemplateRequestDTO request) {

        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException(
                        "Template not found with ID: " + request.getTemplateId()));

        User trainee = userRepository.findById(request.getTraineeId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found with ID: " + request.getTraineeId()));

        if (traineeTemplateRepository.existsByTemplate_TemplateIdAndTrainee_UserId(
                request.getTemplateId(), request.getTraineeId())) {
            throw new RuntimeException(
                    "Template ID: " + request.getTemplateId()
                            + " is already assigned to trainee ID: " + request.getTraineeId());
        }

        TraineeTemplate traineeTemplate = new TraineeTemplate();
        traineeTemplate.setTemplate(template);
        traineeTemplate.setTrainee(trainee);
        TraineeTemplate savedTraineeTemplate = traineeTemplateRepository.save(traineeTemplate);

        // Create trainee-specific snapshot of modules and topics.
        List<Module> templateModules = moduleRepository
                .findByTemplate_TemplateIdOrderBySequenceOrderAsc(template.getTemplateId());
        Map<Long, TraineeModule> traineeModuleByModuleId = new HashMap<>();
        List<TraineeModule> traineeModulesToSave = new ArrayList<>();
        for (Module module : templateModules) {
            TraineeModule traineeModule = new TraineeModule();
            traineeModule.setTraineeTemplate(savedTraineeTemplate);
            traineeModule.setModule(module);
            traineeModulesToSave.add(traineeModule);
        }
        List<TraineeModule> savedTraineeModules = traineeModuleRepository.saveAll(traineeModulesToSave);
        for (TraineeModule savedModule : savedTraineeModules) {
            traineeModuleByModuleId.put(savedModule.getModule().getModuleId(), savedModule);
        }

        List<TraineeTopic> traineeTopicsToSave = new ArrayList<>();
        for (Module module : templateModules) {
            List<Topic> topics = topicRepository.findByModule_ModuleIdOrderBySequenceOrderAsc(module.getModuleId());
            TraineeModule traineeModule = traineeModuleByModuleId.get(module.getModuleId());
            for (Topic topic : topics) {
                TraineeTopic traineeTopic = new TraineeTopic();
                traineeTopic.setTraineeModule(traineeModule);
                traineeTopic.setTopic(topic);
                traineeTopicsToSave.add(traineeTopic);
            }
        }
        if (!traineeTopicsToSave.isEmpty()) {
            traineeTopicRepository.saveAll(traineeTopicsToSave);
        }

        return convertToDTO(savedTraineeTemplate);
    }

    public List<TraineeTemplateResponseDTO> getAllTraineeTemplates() {

        List<TraineeTemplateResponseDTO> traineeTemplates = traineeTemplateRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (traineeTemplates.isEmpty()) {
            throw new RuntimeException("No trainee templates found");
        }

        return traineeTemplates;
    }

    public TraineeTemplateResponseDTO getTraineeTemplateById(Long id) {

        TraineeTemplate traineeTemplate = traineeTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Template not found with ID: " + id));

        return convertToDTO(traineeTemplate);
    }

    public List<TraineeTemplateResponseDTO> getTraineeTemplatesByTrainee(Long traineeId) {

        if (!userRepository.existsById(traineeId)) {
            throw new RuntimeException("User not found with ID: " + traineeId);
        }

        enforceTraineeOwnership(traineeId);

        List<TraineeTemplateResponseDTO> traineeTemplates = traineeTemplateRepository
                .findByTrainee_UserId(traineeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return traineeTemplates;
    }

    public List<TraineeTemplateResponseDTO> getTraineeTemplatesByTemplate(Long templateId) {

        if (!templateRepository.existsById(templateId)) {
            throw new RuntimeException("Template not found with ID: " + templateId);
        }

        List<TraineeTemplateResponseDTO> traineeTemplates = traineeTemplateRepository
                .findByTemplate_TemplateId(templateId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (traineeTemplates.isEmpty()) {
            throw new RuntimeException("No trainee templates found for template ID: " + templateId);
        }

        return traineeTemplates;
    }

    public List<TraineeTemplateResponseDTO> getTraineeTemplatesByStatus(Long traineeId, ProgressStatus status) {

        if (!userRepository.existsById(traineeId)) {
            throw new RuntimeException("User not found with ID: " + traineeId);
        }

        enforceTraineeOwnership(traineeId);

        List<TraineeTemplateResponseDTO> traineeTemplates = traineeTemplateRepository
                .findByTrainee_UserIdAndStatus(traineeId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (traineeTemplates.isEmpty()) {
            throw new RuntimeException("No trainee templates found with status: " + status);
        }

        return traineeTemplates;
    }

    public TraineeTemplateResponseDTO updateTraineeTemplateStatus(Long id, ProgressStatus status) {

        TraineeTemplate traineeTemplate = traineeTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Trainee Template not found with ID: " + id));

        traineeTemplate.setStatus(status);

        return convertToDTO(traineeTemplateRepository.save(traineeTemplate));
    }

    public void deleteTraineeTemplate(Long id) {

        if (!traineeTemplateRepository.existsById(id)) {
            throw new RuntimeException("Trainee Template not found with ID: " + id);
        }

        traineeTemplateRepository.deleteById(id);
    }

    private TraineeTemplateResponseDTO convertToDTO(TraineeTemplate traineeTemplate) {
        return TraineeTemplateResponseDTO.builder()
                .traineeTemplateId(traineeTemplate.getTraineeTemplateId())
                .templateId(traineeTemplate.getTemplate().getTemplateId())
                .templateTitle(traineeTemplate.getTemplate().getTitle())
                .traineeId(traineeTemplate.getTrainee().getUserId())
                .traineeName(traineeTemplate.getTrainee().getName())
                .status(traineeTemplate.getStatus())
                .startedAt(traineeTemplate.getStartedAt())
                .updatedAt(traineeTemplate.getUpdatedAt())
                .build();
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
            throw new RuntimeException("You are not allowed to access another trainee's templates.");
        }
    }
}