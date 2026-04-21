package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TemplateResponseDTO;
import com.example.traineeSheetAutomation.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
@Tag(name = "Template APIs")
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new template (Manager only)")
    public ResponseEntity<TemplateResponseDTO> createTemplate(
            @Valid @RequestBody TemplateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    @Operation(summary = "Get all templates (Manager and Trainee)")
    public ResponseEntity<List<TemplateResponseDTO>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    @Operation(summary = "Get template by ID (Manager and Trainee)")
    public ResponseEntity<TemplateResponseDTO> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('MANAGER') or #userId == authentication.principal.userId")
    @Operation(summary = "Get templates by user (Manager or self)")
    public ResponseEntity<List<TemplateResponseDTO>> getTemplatesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(templateService.getTemplatesByUser(userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update template (Manager only)")
    public ResponseEntity<TemplateResponseDTO> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateRequestDTO request) {
        return ResponseEntity.ok(templateService.updateTemplate(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete template (Manager only)")
    public ResponseEntity<String> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok("Template deleted successfully");
    }
}