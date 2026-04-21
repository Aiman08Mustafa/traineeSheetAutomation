package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.ModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.ModuleResponseDTO;
import com.example.traineeSheetAutomation.entity.Module;
import com.example.traineeSheetAutomation.entity.Template;
import com.example.traineeSheetAutomation.entity.enums.Title;
import com.example.traineeSheetAutomation.repository.ModuleRepository;
import com.example.traineeSheetAutomation.repository.TemplateRepository;
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
public class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private ModuleService moduleService;

    private Module sampleModule;
    private Template sampleTemplate;
    private ModuleRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleTemplate = new Template();
        sampleTemplate.setTemplateId(1L);
        sampleTemplate.setTitle(Title.JAVA);

        sampleModule = new Module();
        sampleModule.setModuleId(1L);
        sampleModule.setTemplate(sampleTemplate);
        sampleModule.setModuleName("Core Java");
        sampleModule.setDescription("Basics of Java");
        sampleModule.setSequenceOrder(1);

        sampleRequest = new ModuleRequestDTO(1L, "Core Java", "Basics of Java", 1);
    }

    // ──────────────────────────── CREATE ────────────────────────────

    @Test
    void createModule_whenCreated_returnsSuccess() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(1L, 1)).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenReturn(sampleModule);

        ModuleResponseDTO result = moduleService.createModule(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getModuleId()).isEqualTo(1L);
        assertThat(result.getModuleName()).isEqualTo("Core Java");
        assertThat(result.getTemplateId()).isEqualTo(1L);

        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void createModule_whenTemplateNotFound_throwsException() {
        when(templateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.createModule(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 1");

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void createModule_whenSequenceOrderConflicts_throwsException() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(1L, 1)).thenReturn(true);

        assertThatThrownBy(() -> moduleService.createModule(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("A module with sequence order 1 already exists in template ID: 1");

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void createModule_mapsAllFieldsCorrectly() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(1L, 1)).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenReturn(sampleModule);

        ModuleResponseDTO result = moduleService.createModule(sampleRequest);

        assertThat(result.getModuleName()).isEqualTo("Core Java");
        assertThat(result.getDescription()).isEqualTo("Basics of Java");
        assertThat(result.getSequenceOrder()).isEqualTo(1);
        assertThat(result.getTemplateTitle()).isEqualTo("JAVA");
    }

    // ──────────────────────────── GET ALL ────────────────────────────

    @Test
    void getAllModules_whenSuccessful_returnsList() {
        when(moduleRepository.findAll()).thenReturn(List.of(sampleModule));

        List<ModuleResponseDTO> result = moduleService.getAllModules();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModuleName()).isEqualTo("Core Java");
    }

    @Test
    void getAllModules_whenEmpty_throwsException() {
        when(moduleRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> moduleService.getAllModules())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No modules found");
    }

    @Test
    void getAllModules_whenMultipleExist_returnsAll() {
        Module second = new Module();
        second.setModuleId(2L);
        second.setTemplate(sampleTemplate);
        second.setModuleName("Advanced Java");
        second.setDescription("Advanced topics");
        second.setSequenceOrder(2);

        when(moduleRepository.findAll()).thenReturn(List.of(sampleModule, second));

        List<ModuleResponseDTO> result = moduleService.getAllModules();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ModuleResponseDTO::getModuleName)
                .containsExactlyInAnyOrder("Core Java", "Advanced Java");
    }

    // ──────────────────────────── GET BY ID ────────────────────────────

    @Test
    void getModuleById_whenExists_returnsModule() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));

        ModuleResponseDTO result = moduleService.getModuleById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getModuleId()).isEqualTo(1L);
        assertThat(result.getModuleName()).isEqualTo("Core Java");
    }

    @Test
    void getModuleById_whenNotFound_throwsException() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.getModuleById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Module not found with ID: 99");
    }

    @Test
    void getModuleById_mapsAllFieldsCorrectly() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));

        ModuleResponseDTO result = moduleService.getModuleById(1L);

        assertThat(result.getModuleId()).isEqualTo(1L);
        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTemplateTitle()).isEqualTo("JAVA");
        assertThat(result.getModuleName()).isEqualTo("Core Java");
        assertThat(result.getDescription()).isEqualTo("Basics of Java");
        assertThat(result.getSequenceOrder()).isEqualTo(1);
    }

    // ──────────────────────────── GET BY TEMPLATE ────────────────────────────

    @Test
    void getModulesByTemplate_whenExists_returnsList() {
        when(templateRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.findByTemplate_TemplateIdOrderBySequenceOrderAsc(1L))
                .thenReturn(List.of(sampleModule));

        List<ModuleResponseDTO> result = moduleService.getModulesByTemplate(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTemplateId()).isEqualTo(1L);
    }

    @Test
    void getModulesByTemplate_whenTemplateNotFound_throwsException() {
        when(templateRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> moduleService.getModulesByTemplate(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 99");

        verify(moduleRepository, never()).findByTemplate_TemplateIdOrderBySequenceOrderAsc(anyLong());
    }

    @Test
    void getModulesByTemplate_whenNoModulesForTemplate_throwsException() {
        when(templateRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.findByTemplate_TemplateIdOrderBySequenceOrderAsc(1L))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> moduleService.getModulesByTemplate(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No modules found for template ID: 1");
    }

    @Test
    void getModulesByTemplate_returnsModulesInSequenceOrder() {
        Module second = new Module();
        second.setModuleId(2L);
        second.setTemplate(sampleTemplate);
        second.setModuleName("Advanced Java");
        second.setDescription("Advanced topics");
        second.setSequenceOrder(2);

        when(templateRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.findByTemplate_TemplateIdOrderBySequenceOrderAsc(1L))
                .thenReturn(List.of(sampleModule, second));

        List<ModuleResponseDTO> result = moduleService.getModulesByTemplate(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSequenceOrder()).isEqualTo(1);
        assertThat(result.get(1).getSequenceOrder()).isEqualTo(2);
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    @Test
    void updateModule_whenValidData_returnsSuccess() {
        ModuleRequestDTO updateRequest = new ModuleRequestDTO(1L, "Advanced Java", "Advanced topics", 2);

        Module updatedModule = new Module();
        updatedModule.setModuleId(1L);
        updatedModule.setTemplate(sampleTemplate);
        updatedModule.setModuleName("Advanced Java");
        updatedModule.setDescription("Advanced topics");
        updatedModule.setSequenceOrder(2);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(1L, 2)).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenReturn(updatedModule);

        ModuleResponseDTO result = moduleService.updateModule(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getModuleName()).isEqualTo("Advanced Java");
        assertThat(result.getSequenceOrder()).isEqualTo(2);
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void updateModule_whenSameSequenceOrderAndSameTemplate_doesNotCheckConflict() {
        // same templateId and same sequenceOrder — both flags false → skip duplicate check
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(moduleRepository.save(any(Module.class))).thenReturn(sampleModule);

        ModuleResponseDTO result = moduleService.updateModule(1L, sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getModuleName()).isEqualTo("Core Java");
        verify(moduleRepository, never())
                .existsByTemplate_TemplateIdAndSequenceOrder(anyLong(), anyInt());
    }

    @Test
    void updateModule_whenModuleNotFound_throwsException() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.updateModule(99L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Module not found with ID: 99");

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void updateModule_whenTemplateNotFound_throwsException() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(templateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.updateModule(1L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 1");

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void updateModule_whenSequenceOrderConflicts_throwsException() {
        ModuleRequestDTO updateRequest = new ModuleRequestDTO(1L, "Core Java", "Basics of Java", 3);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(1L, 3)).thenReturn(true);

        assertThatThrownBy(() -> moduleService.updateModule(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("A module with sequence order 3 already exists in template ID: 1");

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void updateModule_whenTemplateChangedAndSequenceOrderConflicts_throwsException() {
        Template otherTemplate = new Template();
        otherTemplate.setTemplateId(2L);
        otherTemplate.setTitle(Title.JAVA);

        ModuleRequestDTO updateRequest = new ModuleRequestDTO(2L, "Core Java", "Basics of Java", 1);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(sampleModule));
        when(templateRepository.findById(2L)).thenReturn(Optional.of(otherTemplate));
        when(moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(2L, 1)).thenReturn(true);

        assertThatThrownBy(() -> moduleService.updateModule(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("A module with sequence order 1 already exists in template ID: 2");

        verify(moduleRepository, never()).save(any(Module.class));
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Test
    void deleteModule_whenExists_success() {
        when(moduleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(moduleRepository).deleteById(1L);

        moduleService.deleteModule(1L);

        verify(moduleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteModule_whenNotFound_throwsException() {
        when(moduleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> moduleService.deleteModule(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Module not found with ID: 99");

        verify(moduleRepository, never()).deleteById(anyLong());
    }
}