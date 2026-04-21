package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TopicRequestDTO;
import com.example.traineeSheetAutomation.dto.TopicResponseDTO;
import com.example.traineeSheetAutomation.entity.Module;
import com.example.traineeSheetAutomation.entity.Topic;
import com.example.traineeSheetAutomation.entity.TraineeModule;
import com.example.traineeSheetAutomation.entity.TraineeTopic;
import com.example.traineeSheetAutomation.repository.ModuleRepository;
import com.example.traineeSheetAutomation.repository.TopicRepository;
import com.example.traineeSheetAutomation.repository.TraineeModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final TraineeModuleRepository traineeModuleRepository;
    private final TraineeTopicRepository traineeTopicRepository;

    @Transactional
    public TopicResponseDTO createTopic(TopicRequestDTO request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException(
                        "Module not found with ID: " + request.getModuleId()));

        if (topicRepository.existsByModule_ModuleIdAndSequenceOrder(
                request.getModuleId(), request.getSequenceOrder())){
            throw new RuntimeException(
                    "A topic with sequence order " + request.getSequenceOrder()
                            + " already exists in module ID: " + request.getModuleId());
        }

        Topic topic = new Topic();
        topic.setModule(module);
        topic.setTopicName(request.getTopicName());
        topic.setLearningObjective(request.getLearningObjective());
        topic.setReadingMaterial(request.getReadingMaterial());
        topic.setAssignment(request.getAssignment());
        topic.setSequenceOrder(request.getSequenceOrder());

        Topic savedTopic = topicRepository.save(topic);

        // Keep existing trainee module copies in sync with new topics.
        List<TraineeModule> traineeModules = traineeModuleRepository.findByModule_ModuleId(savedTopic.getModule().getModuleId());
        if (!traineeModules.isEmpty()) {
            List<TraineeTopic> traineeTopicsToSave = traineeModules.stream()
                    .filter(tm -> !traineeTopicRepository.existsByTraineeModule_TraineeModuleIdAndTopic_TopicId(
                            tm.getTraineeModuleId(), savedTopic.getTopicId()))
                    .map(tm -> {
                        TraineeTopic traineeTopic = new TraineeTopic();
                        traineeTopic.setTraineeModule(tm);
                        traineeTopic.setTopic(savedTopic);
                        return traineeTopic;
                    })
                    .collect(Collectors.toList());
            if (!traineeTopicsToSave.isEmpty()) {
                traineeTopicRepository.saveAll(traineeTopicsToSave);
            }
        }

        return convertToDTO(savedTopic);
    }

    public List<TopicResponseDTO> getAllTopics(){
        List<TopicResponseDTO> topics = topicRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (topics.isEmpty()){
            throw new RuntimeException("No topics found");
        }

        return topics;
    }

    public TopicResponseDTO getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Topic not found with ID: " + id));

        return convertToDTO(topic);
    }

    public List<TopicResponseDTO> getTopicByModule(Long moduleId) {
        if(!moduleRepository.existsById(moduleId)) {
            throw new RuntimeException("Module not found with ID: " + moduleId);
        }

        return topicRepository
                .findByModule_ModuleIdOrderBySequenceOrderAsc(moduleId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TopicResponseDTO updateTopic(Long id, TopicRequestDTO request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Topic not found with ID: "+ id));

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException(
                        "Module not found with ID: " + request.getModuleId()));

        boolean sequenceChanged = !topic.getSequenceOrder().equals(request.getSequenceOrder());
        boolean moduleChanged = !topic.getModule().getModuleId().equals(request.getModuleId());
        Long oldModuleId = topic.getModule().getModuleId();

        if ((sequenceChanged || moduleChanged) &&
                topicRepository.existsByModule_ModuleIdAndSequenceOrder(
                        request.getModuleId(), request.getSequenceOrder()))  {
            throw new RuntimeException(
                    "A topic with sequence order " + request.getSequenceOrder()
                    + " already exists in module ID: " + request.getModuleId());
        }

        topic.setModule(module);
        topic.setTopicName(request.getTopicName());
        topic.setLearningObjective(request.getLearningObjective());
        topic.setReadingMaterial(request.getReadingMaterial());
        topic.setAssignment(request.getAssignment());
        topic.setSequenceOrder(request.getSequenceOrder());

        Topic savedTopic = topicRepository.save(topic);

        // If topic moved to another module, move trainee-topic copies as well.
        if (moduleChanged) {
            List<TraineeTopic> traineeTopics = traineeTopicRepository.findByTopic_TopicId(savedTopic.getTopicId());
            for (TraineeTopic traineeTopic : traineeTopics) {
                Long traineeTemplateId = traineeTopic.getTraineeModule().getTraineeTemplate().getTraineeTemplateId();
                TraineeModule targetTraineeModule = traineeModuleRepository
                        .findByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(traineeTemplateId, request.getModuleId())
                        .orElseThrow(() -> new RuntimeException(
                                "Trainee module snapshot missing for module ID: " + request.getModuleId()
                        ));
                traineeTopic.setTraineeModule(targetTraineeModule);
            }
            traineeTopicRepository.saveAll(traineeTopics);

            resequenceTopics(oldModuleId);
        }

        return convertToDTO(savedTopic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found with ID: " + id));

        Long moduleId = topic.getModule().getModuleId();
        List<TraineeTopic> traineeTopics = traineeTopicRepository.findByTopic_TopicId(topic.getTopicId());
        if (!traineeTopics.isEmpty()) {
            traineeTopicRepository.deleteAll(traineeTopics);
        }
        topicRepository.delete(topic);
        resequenceTopics(moduleId);
    }

    private void resequenceTopics(Long moduleId) {
        List<Topic> topics = topicRepository.findByModule_ModuleIdOrderBySequenceOrderAsc(moduleId);
        for (int i = 0; i < topics.size(); i++) {
            topics.get(i).setSequenceOrder(i + 1);
        }
        topicRepository.saveAll(topics);
    }

    private TopicResponseDTO convertToDTO(Topic topic) {
        return TopicResponseDTO.builder()
                .topicId(topic.getTopicId())
                .moduleId(topic.getModule().getModuleId())
                .moduleName(topic.getModule().getModuleName())
                .topicName(topic.getTopicName())
                .learningObjectives(topic.getLearningObjective())
                .readingMaterial(topic.getReadingMaterial())
                .assignment(topic.getAssignment())
                .sequenceOrder(topic.getSequenceOrder())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .build();
    }


}
