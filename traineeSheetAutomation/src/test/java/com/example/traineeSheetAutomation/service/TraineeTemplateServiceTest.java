package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TraineeTemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeTemplateResponseDTO;
import com.example.traineeSheetAutomation.entity.Template;
import com.example.traineeSheetAutomation.entity.TraineeTemplate;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;

import com.example.traineeSheetAutomation.entity.enums.Title;
import com.example.traineeSheetAutomation.repository.TemplateRepository;
import com.example.traineeSheetAutomation.repository.TraineeTemplateRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
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
public class TraineeTemplateServiceTest {

    @Mock
    private TraineeTemplateRepository traineeTemplateRepository;

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeTemplateService traineeTemplateService;

    private TraineeTemplate sampleTraineeTemplate;
    private Template sampleTemplate;
    private User sampleTrainee;
    private TraineeTemplateRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleTemplate = new Template();
        sampleTemplate.setTemplateId(1L);
        sampleTemplate.setTitle(Title.JAVA);

        sampleTrainee = new User();
        sampleTrainee.setUserId(1L);
        sampleTrainee.setName("John Doe");

        sampleTraineeTemplate = new TraineeTemplate();
        sampleTraineeTemplate.setTraineeTemplateId(1L);
        sampleTraineeTemplate.setTemplate(sampleTemplate);
        sampleTraineeTemplate.setTrainee(sampleTrainee);
        sampleTraineeTemplate.setStatus(ProgressStatus.NOT_STARTED);

        sampleRequest = new TraineeTemplateRequestDTO(1L, 1L);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createTraineeTemplate_whenCreated_returnsSuccess() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTrainee));
        when(traineeTemplateRepository.existsByTemplate_TemplateIdAndTrainee_UserId(1L, 1L)).thenReturn(false);
        when(traineeTemplateRepository.save(any(TraineeTemplate.class))).thenReturn(sampleTraineeTemplate);

        TraineeTemplateResponseDTO result = traineeTemplateService.createTraineeTemplate(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTraineeTemplateId()).isEqualTo(1L);
        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTraineeId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);

        verify(traineeTemplateRepository, times(1)).save(any(TraineeTemplate.class));
    }

    @Test
    void createTraineeTemplate_whenTemplateNotFound_throwsException() {
        when(templateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeTemplateService.createTraineeTemplate(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 1");

        verify(traineeTemplateRepository, never()).save(any(TraineeTemplate.class));
    }

    @Test
    void createTraineeTemplate_whenTraineeNotFound_throwsException() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeTemplateService.createTraineeTemplate(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 1");

        verify(traineeTemplateRepository, never()).save(any(TraineeTemplate.class));
    }

    @Test
    void createTraineeTemplate_whenDuplicateAssignment_throwsException() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTrainee));
        when(traineeTemplateRepository.existsByTemplate_TemplateIdAndTrainee_UserId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> traineeTemplateService.createTraineeTemplate(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template ID: 1 is already assigned to trainee ID: 1");

        verify(traineeTemplateRepository, never()).save(any(TraineeTemplate.class));
    }

    @Test
    void createTraineeTemplate_mapsAllFieldsCorrectly() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTrainee));
        when(traineeTemplateRepository.existsByTemplate_TemplateIdAndTrainee_UserId(1L, 1L)).thenReturn(false);
        when(traineeTemplateRepository.save(any(TraineeTemplate.class))).thenReturn(sampleTraineeTemplate);

        TraineeTemplateResponseDTO result = traineeTemplateService.createTraineeTemplate(sampleRequest);

        assertThat(result.getTemplateTitle()).isEqualTo("JAVA");
        assertThat(result.getTraineeName()).isEqualTo("John Doe");
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllTraineeTemplates_whenSuccessful_returnsList() {
        when(traineeTemplateRepository.findAll()).thenReturn(List.of(sampleTraineeTemplate));

        List<TraineeTemplateResponseDTO> result = traineeTemplateService.getAllTraineeTemplates();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTraineeName()).isEqualTo("John Doe");
    }

    @Test
    void getAllTraineeTemplates_whenEmpty_throwsException() {
        when(traineeTemplateRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> traineeTemplateService.getAllTraineeTemplates())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No trainee templates found");
    }

    @Test
    void getAllTraineeTemplates_whenMultipleExist_returnsAll() {
        User secondTrainee = new User();
        secondTrainee.setUserId(2L);
        secondTrainee.setName("Jane Doe");

        TraineeTemplate second = new TraineeTemplate();
        second.setTraineeTemplateId(2L);
        second.setTemplate(sampleTemplate);
        second.setTrainee(secondTrainee);
        second.setStatus(ProgressStatus.IN_PROGRESS);

        when(traineeTemplateRepository.findAll()).thenReturn(List.of(sampleTraineeTemplate, second));

        List<TraineeTemplateResponseDTO> result = traineeTemplateService.getAllTraineeTemplates();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TraineeTemplateResponseDTO::getTraineeName)
                .containsExactlyInAnyOrder("John Doe", "Jane Doe");
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getTraineeTemplateById_whenExists_returnsTraineeTemplate() {
        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));

        TraineeTemplateResponseDTO result = traineeTemplateService.getTraineeTemplateById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTraineeTemplateId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    @Test
    void getTraineeTemplateById_whenNotFound_throwsException() {
        when(traineeTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeTemplateService.getTraineeTemplateById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Template not found with ID: 99");
    }

    @Test
    void getTraineeTemplateById_mapsAllFieldsCorrectly() {
        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));

        TraineeTemplateResponseDTO result = traineeTemplateService.getTraineeTemplateById(1L);

        assertThat(result.getTraineeTemplateId()).isEqualTo(1L);
        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTemplateTitle()).isEqualTo("JAVA");
        assertThat(result.getTraineeId()).isEqualTo(1L);
        assertThat(result.getTraineeName()).isEqualTo("John Doe");
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    // ──────────────────────────── GET BY TRAINEE ────────────────────────────

    @Test
    void getTraineeTemplatesByTrainee_whenExists_returnsList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(traineeTemplateRepository.findByTrainee_UserId(1L)).thenReturn(List.of(sampleTraineeTemplate));

        List<TraineeTemplateResponseDTO> result = traineeTemplateService.getTraineeTemplatesByTrainee(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTraineeId()).isEqualTo(1L);
    }

    @Test
    void getTraineeTemplatesByTrainee_whenUserNotFound_throwsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeTemplateService.getTraineeTemplatesByTrainee(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");

        verify(traineeTemplateRepository, never()).findByTrainee_UserId(anyLong());
    }

    @Test
    void getTraineeTemplatesByTrainee_whenNoTemplatesFound_throwsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(traineeTemplateRepository.findByTrainee_UserId(1L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> traineeTemplateService.getTraineeTemplatesByTrainee(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No trainee templates found for trainee ID: 1");
    }

    // ──────────────────────────── GET BY TEMPLATE ────────────────────────────

    @Test
    void getTraineeTemplatesByTemplate_whenExists_returnsList() {
        when(templateRepository.existsById(1L)).thenReturn(true);
        when(traineeTemplateRepository.findByTemplate_TemplateId(1L)).thenReturn(List.of(sampleTraineeTemplate));

        List<TraineeTemplateResponseDTO> result = traineeTemplateService.getTraineeTemplatesByTemplate(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTemplateId()).isEqualTo(1L);
    }

    @Test
    void getTraineeTemplatesByTemplate_whenTemplateNotFound_throwsException() {
        when(templateRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeTemplateService.getTraineeTemplatesByTemplate(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 99");

        verify(traineeTemplateRepository, never()).findByTemplate_TemplateId(anyLong());
    }

    @Test
    void getTraineeTemplatesByTemplate_whenNoTemplatesFound_throwsException() {
        when(templateRepository.existsById(1L)).thenReturn(true);
        when(traineeTemplateRepository.findByTemplate_TemplateId(1L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> traineeTemplateService.getTraineeTemplatesByTemplate(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No trainee templates found for template ID: 1");
    }

    // ──────────────────────────── GET BY STATUS ────────────────────────────

    @Test
    void getTraineeTemplatesByStatus_whenExists_returnsList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(traineeTemplateRepository.findByTrainee_UserIdAndStatus(1L, ProgressStatus.NOT_STARTED))
                .thenReturn(List.of(sampleTraineeTemplate));

        List<TraineeTemplateResponseDTO> result = traineeTemplateService.getTraineeTemplatesByStatus(1L, ProgressStatus.NOT_STARTED);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    @Test
    void getTraineeTemplatesByStatus_whenUserNotFound_throwsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeTemplateService.getTraineeTemplatesByStatus(99L, ProgressStatus.NOT_STARTED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");

        verify(traineeTemplateRepository, never()).findByTrainee_UserIdAndStatus(anyLong(), any());
    }

    @Test
    void getTraineeTemplatesByStatus_whenNoTemplatesFoundWithStatus_throwsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(traineeTemplateRepository.findByTrainee_UserIdAndStatus(1L, ProgressStatus.COMPLETED))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> traineeTemplateService.getTraineeTemplatesByStatus(1L, ProgressStatus.COMPLETED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No trainee templates found with status: COMPLETED");
    }

    // ──────────────────────────── UPDATE STATUS ────────────────────────────

    @Test
    void updateTraineeTemplateStatus_whenValidData_returnsSuccess() {
        TraineeTemplate updatedTemplate = new TraineeTemplate();
        updatedTemplate.setTraineeTemplateId(1L);
        updatedTemplate.setTemplate(sampleTemplate);
        updatedTemplate.setTrainee(sampleTrainee);
        updatedTemplate.setStatus(ProgressStatus.IN_PROGRESS);

        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));
        when(traineeTemplateRepository.save(any(TraineeTemplate.class))).thenReturn(updatedTemplate);

        TraineeTemplateResponseDTO result = traineeTemplateService.updateTraineeTemplateStatus(1L, ProgressStatus.IN_PROGRESS);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.IN_PROGRESS);
        verify(traineeTemplateRepository, times(1)).save(any(TraineeTemplate.class));
    }

    @Test
    void updateTraineeTemplateStatus_whenNotFound_throwsException() {
        when(traineeTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeTemplateService.updateTraineeTemplateStatus(99L, ProgressStatus.IN_PROGRESS))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Template not found with ID: 99");

        verify(traineeTemplateRepository, never()).save(any(TraineeTemplate.class));
    }

    @Test
    void updateTraineeTemplateStatus_toCompleted_returnsSuccess() {
        TraineeTemplate completedTemplate = new TraineeTemplate();
        completedTemplate.setTraineeTemplateId(1L);
        completedTemplate.setTemplate(sampleTemplate);
        completedTemplate.setTrainee(sampleTrainee);
        completedTemplate.setStatus(ProgressStatus.COMPLETED);

        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));
        when(traineeTemplateRepository.save(any(TraineeTemplate.class))).thenReturn(completedTemplate);

        TraineeTemplateResponseDTO result = traineeTemplateService.updateTraineeTemplateStatus(1L, ProgressStatus.COMPLETED);

        assertThat(result.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteTraineeTemplate_whenExists_success() {
        when(traineeTemplateRepository.existsById(1L)).thenReturn(true);
        doNothing().when(traineeTemplateRepository).deleteById(1L);

        traineeTemplateService.deleteTraineeTemplate(1L);

        verify(traineeTemplateRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTraineeTemplate_whenNotFound_throwsException() {
        when(traineeTemplateRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeTemplateService.deleteTraineeTemplate(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Template not found with ID: 99");

        verify(traineeTemplateRepository, never()).deleteById(anyLong());
    }
}