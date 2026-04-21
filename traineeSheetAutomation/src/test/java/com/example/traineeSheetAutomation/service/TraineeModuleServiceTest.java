package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TraineeModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeModuleResponseDTO;
import com.example.traineeSheetAutomation.entity.Module;
import com.example.traineeSheetAutomation.entity.TraineeModule;
import com.example.traineeSheetAutomation.entity.TraineeTemplate;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.repository.ModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeTemplateRepository;
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
public class TraineeModuleServiceTest {

    @Mock
    private TraineeModuleRepository traineeModuleRepository;

    @Mock
    private TraineeTemplateRepository traineeTemplateRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private TraineeModuleService traineeModuleService;

    private TraineeModule sampleTraineeModule;
    private TraineeTemplate sampleTraineeTemplate;
    private Module sampleModule;
    private TraineeModuleRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleTraineeTemplate = new TraineeTemplate();
        sampleTraineeTemplate.setTraineeTemplateId(1L);

        sampleModule = new Module();
        sampleModule.setModuleId(1L);
        sampleModule.setModuleName("Core Java");

        sampleTraineeModule = new TraineeModule();
        sampleTraineeModule.setTraineeModuleId(1L);
        sampleTraineeModule.setTraineeTemplate(sampleTraineeTemplate);
        sampleTraineeModule.setModule(sampleModule);
        sampleTraineeModule.setStatus(ProgressStatus.NOT_STARTED);

        sampleRequest = new TraineeModuleRequestDTO(1L, 1L, null);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createTraineeModule_whenCreated_returnsSuccess() {
        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(traineeModuleRepository.existsByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(1L, 1L)).thenReturn(false);
        when(traineeModuleRepository.save(any(TraineeModule.class))).thenReturn(sampleTraineeModule);

        TraineeModuleResponseDTO result = traineeModuleService.createTraineeModule(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTraineeModuleId()).isEqualTo(1L);
        assertThat(result.getTraineeTemplateId()).isEqualTo(1L);
        assertThat(result.getModuleId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);

        verify(traineeModuleRepository, times(1)).save(any(TraineeModule.class));
    }

    @Test
    void createTraineeModule_whenTraineeTemplateNotFound_throwsException() {
        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeModuleService.createTraineeModule(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Template not found with ID: 1");

        verify(traineeModuleRepository, never()).save(any(TraineeModule.class));
    }

    @Test
    void createTraineeModule_whenModuleNotFound_throwsException() {
        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeModuleService.createTraineeModule(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Module not found with ID: 1");

        verify(traineeModuleRepository, never()).save(any(TraineeModule.class));
    }

    @Test
    void createTraineeModule_whenDuplicateAssignment_throwsException() {
        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(traineeModuleRepository.existsByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> traineeModuleService.createTraineeModule(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Module already exists for trainee template ID: 1 and module ID: 1");

        verify(traineeModuleRepository, never()).save(any(TraineeModule.class));
    }

    @Test
    void createTraineeModule_mapsAllFieldsCorrectly() {
        when(traineeTemplateRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeTemplate));
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(traineeModuleRepository.existsByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(1L, 1L)).thenReturn(false);
        when(traineeModuleRepository.save(any(TraineeModule.class))).thenReturn(sampleTraineeModule);

        TraineeModuleResponseDTO result = traineeModuleService.createTraineeModule(sampleRequest);

        assertThat(result.getModuleName()).isEqualTo("Core Java");
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllTraineeModules_whenSuccessful_returnsList() {
        when(traineeModuleRepository.findAll()).thenReturn(List.of(sampleTraineeModule));

        List<TraineeModuleResponseDTO> result = traineeModuleService.getAllTraineeModules();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModuleName()).isEqualTo("Core Java");
    }

    @Test
    void getAllTraineeModules_whenEmpty_throwsException() {
        when(traineeModuleRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> traineeModuleService.getAllTraineeModules())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No trainee modules found");
    }

    @Test
    void getAllTraineeModules_whenMultipleExist_returnsAll() {
        Module secondModule = new Module();
        secondModule.setModuleId(2L);
        secondModule.setModuleName("Advanced Java");

        TraineeModule second = new TraineeModule();
        second.setTraineeModuleId(2L);
        second.setTraineeTemplate(sampleTraineeTemplate);
        second.setModule(secondModule);
        second.setStatus(ProgressStatus.IN_PROGRESS);

        when(traineeModuleRepository.findAll()).thenReturn(List.of(sampleTraineeModule, second));

        List<TraineeModuleResponseDTO> result = traineeModuleService.getAllTraineeModules();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TraineeModuleResponseDTO::getModuleName)
                .containsExactlyInAnyOrder("Core Java", "Advanced Java");
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getTraineeModuleById_whenExists_returnsTraineeModule() {
        when(traineeModuleRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeModule));

        TraineeModuleResponseDTO result = traineeModuleService.getTraineeModuleById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTraineeModuleId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    @Test
    void getTraineeModuleById_whenNotFound_throwsException() {
        when(traineeModuleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeModuleService.getTraineeModuleById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Module not found with ID: 99");
    }

    @Test
    void getTraineeModuleById_mapsAllFieldsCorrectly() {
        when(traineeModuleRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeModule));

        TraineeModuleResponseDTO result = traineeModuleService.getTraineeModuleById(1L);

        assertThat(result.getTraineeModuleId()).isEqualTo(1L);
        assertThat(result.getTraineeTemplateId()).isEqualTo(1L);
        assertThat(result.getModuleId()).isEqualTo(1L);
        assertThat(result.getModuleName()).isEqualTo("Core Java");
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    // ──────────────────────────── GET BY TRAINEE TEMPLATE ────────────────────────────

    @Test
    void getTraineeModulesByTraineeTemplate_whenExists_returnsList() {
        when(traineeTemplateRepository.existsById(1L)).thenReturn(true);
        when(traineeModuleRepository.findByTraineeTemplate_TraineeTemplateId(1L))
                .thenReturn(List.of(sampleTraineeModule));

        List<TraineeModuleResponseDTO> result = traineeModuleService.getTraineeModulesByTraineeTemplate(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTraineeTemplateId()).isEqualTo(1L);
    }

    @Test
    void getTraineeModulesByTraineeTemplate_whenTraineeTemplateNotFound_throwsException() {
        when(traineeTemplateRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeModuleService.getTraineeModulesByTraineeTemplate(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Template not found with ID: 99");

        verify(traineeModuleRepository, never()).findByTraineeTemplate_TraineeTemplateId(anyLong());
    }

    @Test
    void getTraineeModulesByTraineeTemplate_whenNoModulesFound_throwsException() {
        when(traineeTemplateRepository.existsById(1L)).thenReturn(true);
        when(traineeModuleRepository.findByTraineeTemplate_TraineeTemplateId(1L))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> traineeModuleService.getTraineeModulesByTraineeTemplate(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No trainee modules found for trainee template ID: 1");
    }

    // ──────────────────────────── GET BY STATUS ────────────────────────────

    @Test
    void getTraineeModulesByStatus_whenExists_returnsList() {
        when(traineeTemplateRepository.existsById(1L)).thenReturn(true);
        when(traineeModuleRepository.findByTraineeTemplate_TraineeTemplateIdAndStatus(1L, ProgressStatus.NOT_STARTED))
                .thenReturn(List.of(sampleTraineeModule));

        List<TraineeModuleResponseDTO> result = traineeModuleService.getTraineeModulesByStatus(1L, ProgressStatus.NOT_STARTED);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ProgressStatus.NOT_STARTED);
    }

    @Test
    void getTraineeModulesByStatus_whenTraineeTemplateNotFound_throwsException() {
        when(traineeTemplateRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeModuleService.getTraineeModulesByStatus(99L, ProgressStatus.NOT_STARTED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Template not found with ID: 99");

        verify(traineeModuleRepository, never())
                .findByTraineeTemplate_TraineeTemplateIdAndStatus(anyLong(), any());
    }

    @Test
    void getTraineeModulesByStatus_whenNoModulesFoundWithStatus_throwsException() {
        when(traineeTemplateRepository.existsById(1L)).thenReturn(true);
        when(traineeModuleRepository.findByTraineeTemplate_TraineeTemplateIdAndStatus(1L, ProgressStatus.COMPLETED))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> traineeModuleService.getTraineeModulesByStatus(1L, ProgressStatus.COMPLETED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No trainee modules found with status: COMPLETED");
    }

    // ──────────────────────────── UPDATE STATUS ────────────────────────────

    @Test
    void updateTraineeModuleStatus_whenValidData_returnsSuccess() {
        TraineeModule updatedModule = new TraineeModule();
        updatedModule.setTraineeModuleId(1L);
        updatedModule.setTraineeTemplate(sampleTraineeTemplate);
        updatedModule.setModule(sampleModule);
        updatedModule.setStatus(ProgressStatus.IN_PROGRESS);

        when(traineeModuleRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeModule));
        when(traineeModuleRepository.save(any(TraineeModule.class))).thenReturn(updatedModule);

        TraineeModuleResponseDTO result = traineeModuleService.updateTraineeModuleStatus(1L, ProgressStatus.IN_PROGRESS);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ProgressStatus.IN_PROGRESS);
        verify(traineeModuleRepository, times(1)).save(any(TraineeModule.class));
    }

    @Test
    void updateTraineeModuleStatus_whenNotFound_throwsException() {
        when(traineeModuleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeModuleService.updateTraineeModuleStatus(99L, ProgressStatus.IN_PROGRESS))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Module not found with ID: 99");

        verify(traineeModuleRepository, never()).save(any(TraineeModule.class));
    }

    @Test
    void updateTraineeModuleStatus_toCompleted_returnsSuccess() {
        TraineeModule completedModule = new TraineeModule();
        completedModule.setTraineeModuleId(1L);
        completedModule.setTraineeTemplate(sampleTraineeTemplate);
        completedModule.setModule(sampleModule);
        completedModule.setStatus(ProgressStatus.COMPLETED);

        when(traineeModuleRepository.findById(1L)).thenReturn(Optional.of(sampleTraineeModule));
        when(traineeModuleRepository.save(any(TraineeModule.class))).thenReturn(completedModule);

        TraineeModuleResponseDTO result = traineeModuleService.updateTraineeModuleStatus(1L, ProgressStatus.COMPLETED);

        assertThat(result.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteTraineeModule_whenExists_success() {
        when(traineeModuleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(traineeModuleRepository).deleteById(1L);

        traineeModuleService.deleteTraineeModule(1L);

        verify(traineeModuleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTraineeModule_whenNotFound_throwsException() {
        when(traineeModuleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeModuleService.deleteTraineeModule(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee Module not found with ID: 99");

        verify(traineeModuleRepository, never()).deleteById(anyLong());
    }
}