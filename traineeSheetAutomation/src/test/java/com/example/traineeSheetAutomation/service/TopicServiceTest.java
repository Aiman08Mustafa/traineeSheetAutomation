package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TopicRequestDTO;
import com.example.traineeSheetAutomation.dto.TopicResponseDTO;
import com.example.traineeSheetAutomation.entity.Module;
import com.example.traineeSheetAutomation.entity.Topic;
import com.example.traineeSheetAutomation.repository.ModuleRepository;
import com.example.traineeSheetAutomation.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private TopicService topicService;

    private Topic sampleTopic;
    private Module sampleModule;
    private TopicRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleModule = new Module();
        sampleModule.setModuleId(1L);
        sampleModule.setModuleName("Core Java");

        sampleTopic = new Topic();
        sampleTopic.setTopicId(1L);
        sampleTopic.setModule(sampleModule);
        sampleTopic.setTopicName("OOP Concepts");
        sampleTopic.setLearningObjective("Understand OOP");
        sampleTopic.setReadingMaterial("Chapter 1");
        sampleTopic.setAssignment("Assignment 1");
        sampleTopic.setSequenceOrder(1);

        sampleRequest = new TopicRequestDTO(1L, "OOP Concepts", "Understand OOP", "Chapter 1", "Assignment 1", 1);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createTopic_whenCreated_returnsSuccess() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(topicRepository.existsByModule_ModuleIdAndSequenceOrder(1L, 1)).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenReturn(sampleTopic);

        TopicResponseDTO result = topicService.createTopic(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTopicId()).isEqualTo(1L);
        assertThat(result.getTopicName()).isEqualTo("OOP Concepts");
        assertThat(result.getModuleId()).isEqualTo(1L);

        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void createTopic_whenModuleNotFound_throwsException() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.createTopic(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Module not found with ID: 1");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void createTopic_whenSequenceOrderConflicts_throwsException() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(topicRepository.existsByModule_ModuleIdAndSequenceOrder(1L, 1)).thenReturn(true);

        assertThatThrownBy(() -> topicService.createTopic(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("A topic with sequence order 1 already exists in module ID: 1");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void createTopic_mapsAllFieldsCorrectly() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(topicRepository.existsByModule_ModuleIdAndSequenceOrder(1L, 1)).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenReturn(sampleTopic);

        TopicResponseDTO result = topicService.createTopic(sampleRequest);

        assertThat(result.getTopicName()).isEqualTo("OOP Concepts");
        assertThat(result.getLearningObjectives()).isEqualTo("Understand OOP");
        assertThat(result.getReadingMaterial()).isEqualTo("Chapter 1");
        assertThat(result.getAssignment()).isEqualTo("Assignment 1");
        assertThat(result.getSequenceOrder()).isEqualTo(1);
        assertThat(result.getModuleName()).isEqualTo("Core Java");
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllTopics_whenSuccessful_returnsList() {
        when(topicRepository.findAll()).thenReturn(List.of(sampleTopic));

        List<TopicResponseDTO> result = topicService.getAllTopics();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTopicName()).isEqualTo("OOP Concepts");
    }

    @Test
    void getAllTopics_whenEmpty_throwsException() {
        when(topicRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> topicService.getAllTopics())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No topics found");
    }

    @Test
    void getAllTopics_whenMultipleExist_returnsAll() {
        Topic second = new Topic();
        second.setTopicId(2L);
        second.setModule(sampleModule);
        second.setTopicName("Collections");
        second.setSequenceOrder(2);

        when(topicRepository.findAll()).thenReturn(List.of(sampleTopic, second));

        List<TopicResponseDTO> result = topicService.getAllTopics();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TopicResponseDTO::getTopicName)
                .containsExactlyInAnyOrder("OOP Concepts", "Collections");
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getTopicById_whenExists_returnsTopic() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));

        TopicResponseDTO result = topicService.getTopicById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTopicId()).isEqualTo(1L);
        assertThat(result.getTopicName()).isEqualTo("OOP Concepts");
    }

    @Test
    void getTopicById_whenNotFound_throwsException() {
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.getTopicById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Topic not found with ID: 99");
    }

    @Test
    void getTopicById_mapsAllFieldsCorrectly() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));

        TopicResponseDTO result = topicService.getTopicById(1L);

        assertThat(result.getTopicId()).isEqualTo(1L);
        assertThat(result.getModuleId()).isEqualTo(1L);
        assertThat(result.getModuleName()).isEqualTo("Core Java");
        assertThat(result.getTopicName()).isEqualTo("OOP Concepts");
        assertThat(result.getLearningObjectives()).isEqualTo("Understand OOP");
        assertThat(result.getReadingMaterial()).isEqualTo("Chapter 1");
        assertThat(result.getAssignment()).isEqualTo("Assignment 1");
        assertThat(result.getSequenceOrder()).isEqualTo(1);
    }

    // ──────────────────────────── GET BY MODULE ────────────────────────────

    @Test
    void getTopicByModule_whenExists_returnsList() {
        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(topicRepository.findByModule_ModuleIdOrderBySequenceOrderAsc(1L))
                .thenReturn(List.of(sampleTopic));

        List<TopicResponseDTO> result = topicService.getTopicByModule(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModuleId()).isEqualTo(1L);
    }

    @Test
    void getTopicByModule_whenModuleNotFound_throwsException() {
        when(moduleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> topicService.getTopicByModule(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Module not found with ID: 99");

        verify(topicRepository, never()).findByModule_ModuleIdOrderBySequenceOrderAsc(anyLong());
    }

    @Test
    void getTopicByModule_whenNoTopicsForModule_throwsException() {
        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(topicRepository.findByModule_ModuleIdOrderBySequenceOrderAsc(1L))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> topicService.getTopicByModule(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No topics found for module ID: 1");
    }

    @Test
    void getTopicByModule_returnsTopicsInSequenceOrder() {
        Topic second = new Topic();
        second.setTopicId(2L);
        second.setModule(sampleModule);
        second.setTopicName("Collections");
        second.setSequenceOrder(2);

        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(topicRepository.findByModule_ModuleIdOrderBySequenceOrderAsc(1L))
                .thenReturn(List.of(sampleTopic, second));

        List<TopicResponseDTO> result = topicService.getTopicByModule(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSequenceOrder()).isEqualTo(1);
        assertThat(result.get(1).getSequenceOrder()).isEqualTo(2);
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    @Test
    void updateTopic_whenValidData_returnsSuccess() {
        TopicRequestDTO updateRequest = new TopicRequestDTO(1L, "Collections", "Understand Collections", "Chapter 2", "Assignment 2", 2);

        Topic updatedTopic = new Topic();
        updatedTopic.setTopicId(1L);
        updatedTopic.setModule(sampleModule);
        updatedTopic.setTopicName("Collections");
        updatedTopic.setLearningObjective("Understand Collections");
        updatedTopic.setReadingMaterial("Chapter 2");
        updatedTopic.setAssignment("Assignment 2");
        updatedTopic.setSequenceOrder(2);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(topicRepository.existsByModule_ModuleIdAndSequenceOrder(1L, 2)).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenReturn(updatedTopic);

        TopicResponseDTO result = topicService.updateTopic(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTopicName()).isEqualTo("Collections");
        assertThat(result.getSequenceOrder()).isEqualTo(2);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void updateTopic_whenSameSequenceOrderAndSameModule_doesNotCheckConflict() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(topicRepository.save(any(Topic.class))).thenReturn(sampleTopic);

        TopicResponseDTO result = topicService.updateTopic(1L, sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTopicName()).isEqualTo("OOP Concepts");
        verify(topicRepository, never()).existsByModule_ModuleIdAndSequenceOrder(anyLong(), anyInt());
    }

    @Test
    void updateTopic_whenTopicNotFound_throwsException() {
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.updateTopic(99L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Topic not found with ID: 99");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void updateTopic_whenModuleNotFound_throwsException() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.updateTopic(1L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Module not found with ID: 1");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void updateTopic_whenSequenceOrderConflicts_throwsException() {
        TopicRequestDTO updateRequest = new TopicRequestDTO(1L, "OOP Concepts", "Understand OOP", "Chapter 1", "Assignment 1", 3);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(topicRepository.existsByModule_ModuleIdAndSequenceOrder(1L, 3)).thenReturn(true);

        assertThatThrownBy(() -> topicService.updateTopic(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("A topic with sequence order 3 already exists in module ID: 1");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void updateTopic_whenModuleChangedAndSequenceOrderConflicts_throwsException() {
        Module otherModule = new Module();
        otherModule.setModuleId(2L);
        otherModule.setModuleName("Advanced Java");

        TopicRequestDTO updateRequest = new TopicRequestDTO(2L, "OOP Concepts", "Understand OOP", "Chapter 1", "Assignment 1", 1);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(sampleTopic));
        when(moduleRepository.findById(2L)).thenReturn(Optional.of(otherModule));
        when(topicRepository.existsByModule_ModuleIdAndSequenceOrder(2L, 1)).thenReturn(true);

        assertThatThrownBy(() -> topicService.updateTopic(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("A topic with sequence order 1 already exists in module ID: 2");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteTopic_whenExists_success() {
        when(topicRepository.existsById(1L)).thenReturn(true);
        doNothing().when(topicRepository).deleteById(1L);

        topicService.deleteTopic(1L);

        verify(topicRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTopic_whenNotFound_throwsException() {
        when(topicRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> topicService.deleteTopic(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Topic not found with ID: 99");

        verify(topicRepository, never()).deleteById(anyLong());
    }
}