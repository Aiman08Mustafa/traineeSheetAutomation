package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TraineeTemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeTemplateResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.service.TraineeTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainee-templates")
@RequiredArgsConstructor
@Tag(name = "Trainee Template APIs")
public class TraineeTemplateController {

    private final TraineeTemplateService traineeTemplateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<TraineeTemplateResponseDTO> createTraineeTemplate(
            @Valid @RequestBody TraineeTemplateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeTemplateService.createTraineeTemplate(request));
    }

    @GetMapping
    public ResponseEntity<List<TraineeTemplateResponseDTO>> getAllTraineeTemplates() {
        return ResponseEntity.ok(traineeTemplateService.getAllTraineeTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TraineeTemplateResponseDTO> getTraineeTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(traineeTemplateService.getTraineeTemplateById(id));
    }

    @GetMapping("/trainee/{traineeId}")
    @PreAuthorize("hasRole('MANAGER') or #traineeId == authentication.principal.userId")
    public ResponseEntity<List<TraineeTemplateResponseDTO>> getTraineeTemplatesByTrainee(
            @PathVariable Long traineeId) {
        return ResponseEntity.ok(traineeTemplateService.getTraineeTemplatesByTrainee(traineeId));
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<TraineeTemplateResponseDTO>> getTraineeTemplatesByTemplate(
            @PathVariable Long templateId) {
        return ResponseEntity.ok(traineeTemplateService.getTraineeTemplatesByTemplate(templateId));
    }

    @GetMapping("/trainee/{traineeId}/status")
    public ResponseEntity<List<TraineeTemplateResponseDTO>> getTraineeTemplatesByStatus(
            @PathVariable Long traineeId,
            @RequestParam ProgressStatus status) {
        return ResponseEntity.ok(traineeTemplateService.getTraineeTemplatesByStatus(traineeId, status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TraineeTemplateResponseDTO> updateTraineeTemplateStatus(
            @PathVariable Long id,
            @RequestParam ProgressStatus status) {
        return ResponseEntity.ok(traineeTemplateService.updateTraineeTemplateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTraineeTemplate(@PathVariable Long id) {
        traineeTemplateService.deleteTraineeTemplate(id);
        return ResponseEntity.ok("Trainee Template deleted successfully");
    }
}