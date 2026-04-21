package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TemplateResponseDTO;
import com.example.traineeSheetAutomation.entity.ServiceLine;
import com.example.traineeSheetAutomation.entity.Template;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.repository.ServiceLineRepository;
import com.example.traineeSheetAutomation.repository.TemplateRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final ServiceLineRepository serviceLineRepository;

    private String normalizeTitle(String title) {
        if (title == null) {
            return null;
        }
        return title.trim();
    }

    public TemplateResponseDTO createTemplate(TemplateRequestDTO request) {
        String normalizedTitle = normalizeTitle(request.getTitle());

        if (templateRepository.existsByTitleIgnoreCase(normalizedTitle)) {
            throw new RuntimeException("Template with title already exists: " + normalizedTitle);
        }

        User createdBy = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException(
                        "User not found with ID: " + request.getCreatedBy()));

        ServiceLine serviceLine = serviceLineRepository.findById(request.getServiceLineId())
                .orElseThrow(() -> new RuntimeException(
                        "Service Line not found with ID: " + request.getServiceLineId()));

        Template template = new Template();
        template.setTitle(normalizedTitle);
        template.setDescription(request.getDescription());
        template.setCreatedBy(createdBy);
        template.setServiceLine(serviceLine);

        return convertToDTO(templateRepository.save(template));
    }

    public List<TemplateResponseDTO> getAllTemplates() {

        List<TemplateResponseDTO> templates = templateRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (templates.isEmpty()) {
            throw new RuntimeException("No templates found");
        }

        return templates;
    }

    public TemplateResponseDTO getTemplateById(Long id) {

        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Template not found with ID: " + id));

        return convertToDTO(template);
    }

    public List<TemplateResponseDTO> getTemplatesByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        List<TemplateResponseDTO> templates = templateRepository.findByCreatedBy_UserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (templates.isEmpty()) {
            throw new RuntimeException("No templates found for user ID: " + userId);
        }

        return templates;
    }

    public TemplateResponseDTO updateTemplate(Long id, TemplateRequestDTO request) {
        String normalizedTitle = normalizeTitle(request.getTitle());

        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Template not found with ID: " + id));

        // Only check title conflict if the title is actually being changed
        if (!template.getTitle().equalsIgnoreCase(normalizedTitle) &&
                templateRepository.existsByTitleIgnoreCase(normalizedTitle)) {
            throw new RuntimeException("Template with title already exists: " + normalizedTitle);
        }

        User createdBy = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException(
                        "User not found with ID: " + request.getCreatedBy()));

        ServiceLine serviceLine = serviceLineRepository.findById(request.getServiceLineId())
                .orElseThrow(() -> new RuntimeException(
                        "Service Line not found with ID: " + request.getServiceLineId()));

        template.setTitle(normalizedTitle);
        template.setDescription(request.getDescription());
        template.setCreatedBy(createdBy);
        template.setServiceLine(serviceLine);

        return convertToDTO(templateRepository.save(template));
    }

    public void deleteTemplate(Long id) {

        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found with ID: " + id);
        }

        templateRepository.deleteById(id);
    }

    private TemplateResponseDTO convertToDTO(Template template) {
        return TemplateResponseDTO.builder()
                .templateId(template.getTemplateId())
                .title(template.getTitle())
                .description(template.getDescription())
                .createdBy(template.getCreatedBy().getUserId())
                .createdByName(template.getCreatedBy().getName())
                .serviceLineId(template.getServiceLine().getServiceLineID())
                .serviceLineDepartment(template.getServiceLine().getDepartment().name())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}