package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.ModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.ModuleResponseDTO;
import com.example.traineeSheetAutomation.entity.Module;
import com.example.traineeSheetAutomation.entity.Template;
import com.example.traineeSheetAutomation.entity.TraineeModule;
import com.example.traineeSheetAutomation.entity.TraineeTemplate;
import com.example.traineeSheetAutomation.repository.ModuleRepository;
import com.example.traineeSheetAutomation.repository.TemplateRepository;
import com.example.traineeSheetAutomation.repository.TraineeModuleRepository;
import com.example.traineeSheetAutomation.repository.TraineeTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final TemplateRepository templateRepository;
    private final TraineeTemplateRepository traineeTemplateRepository;
    private final TraineeModuleRepository traineeModuleRepository;

    @Transactional
    public ModuleResponseDTO createModule(ModuleRequestDTO request) {

        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException(
                        "Template not found with ID: " + request.getTemplateId()));

        if (moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(
                request.getTemplateId(), request.getSequenceOrder())) {
            throw new RuntimeException(
                    "A module with sequence order " + request.getSequenceOrder()
                            + " already exists in template ID: " + request.getTemplateId());
        }

        Module module = new Module();
        module.setTemplate(template);
        module.setModuleName(request.getModuleName());
        module.setDescription(request.getDescription());
        module.setSequenceOrder(request.getSequenceOrder());

        Module savedModule = moduleRepository.save(module);

        // Keep already-assigned trainee templates in sync with new modules.
        List<TraineeTemplate> assignedTraineeTemplates = traineeTemplateRepository
                .findByTemplate_TemplateId(savedModule.getTemplate().getTemplateId());
        if (!assignedTraineeTemplates.isEmpty()) {
            List<TraineeModule> traineeModulesToSave = assignedTraineeTemplates.stream()
                    .filter(tt -> !traineeModuleRepository.existsByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(
                            tt.getTraineeTemplateId(), savedModule.getModuleId()))
                    .map(tt -> {
                        TraineeModule traineeModule = new TraineeModule();
                        traineeModule.setTraineeTemplate(tt);
                        traineeModule.setModule(savedModule);
                        return traineeModule;
                    })
                    .collect(Collectors.toList());
            if (!traineeModulesToSave.isEmpty()) {
                traineeModuleRepository.saveAll(traineeModulesToSave);
            }
        }

        return convertToDTO(savedModule);
    }

    public List<ModuleResponseDTO> getAllModules() {

        List<ModuleResponseDTO> modules = moduleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (modules.isEmpty()) {
            throw new RuntimeException("No modules found");
        }

        return modules;
    }

    public ModuleResponseDTO getModuleById(Long id) {

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Module not found with ID: " + id));

        return convertToDTO(module);
    }

    public List<ModuleResponseDTO> getModulesByTemplate(Long templateId) {

        if (!templateRepository.existsById(templateId)) {
            throw new RuntimeException("Template not found with ID: " + templateId);
        }

        return moduleRepository
                .findByTemplate_TemplateIdOrderBySequenceOrderAsc(templateId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ModuleResponseDTO updateModule(Long id, ModuleRequestDTO request) {

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Module not found with ID: " + id));

        Template template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException(
                        "Template not found with ID: " + request.getTemplateId()));

        // Check sequence order conflict only if it changed within the same template
        boolean sequenceChanged = !module.getSequenceOrder().equals(request.getSequenceOrder());
        boolean templateChanged = !module.getTemplate().getTemplateId().equals(request.getTemplateId());

        if ((sequenceChanged || templateChanged) &&
                moduleRepository.existsByTemplate_TemplateIdAndSequenceOrder(
                        request.getTemplateId(), request.getSequenceOrder())) {
            throw new RuntimeException(
                    "A module with sequence order " + request.getSequenceOrder()
                            + " already exists in template ID: " + request.getTemplateId());
        }

        module.setTemplate(template);
        module.setModuleName(request.getModuleName());
        module.setDescription(request.getDescription());
        module.setSequenceOrder(request.getSequenceOrder());

        return convertToDTO(moduleRepository.save(module));
    }

    @Transactional
    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with ID: " + id));

        Long templateId = module.getTemplate().getTemplateId();
        List<TraineeModule> traineeModules = traineeModuleRepository.findByModule_ModuleId(module.getModuleId());
        if (!traineeModules.isEmpty()) {
            traineeModuleRepository.deleteAll(traineeModules);
        }
        moduleRepository.delete(module);
        resequenceModules(templateId);
    }

    private void resequenceModules(Long templateId) {
        List<Module> modules = moduleRepository.findByTemplate_TemplateIdOrderBySequenceOrderAsc(templateId);
        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).setSequenceOrder(i + 1);
        }
        moduleRepository.saveAll(modules);
    }

    private ModuleResponseDTO convertToDTO(Module module) {
        return ModuleResponseDTO.builder()
                .moduleId(module.getModuleId())
                .templateId(module.getTemplate().getTemplateId())
                .templateTitle(module.getTemplate().getTitle())
                .moduleName(module.getModuleName())
                .description(module.getDescription())
                .sequenceOrder(module.getSequenceOrder())
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }
}