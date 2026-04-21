package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TraineeModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeModuleResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.service.TraineeModuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainee-modules")
@RequiredArgsConstructor
@Tag(name = "Trainee Module APIs")
public class TraineeModuleController {

    private final TraineeModuleService traineeModuleService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TraineeModuleResponseDTO> createTraineeModule(
            @Valid @RequestBody TraineeModuleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeModuleService.createTraineeModule(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<TraineeModuleResponseDTO>> getAllTraineeModules() {
        return ResponseEntity.ok(traineeModuleService.getAllTraineeModules());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<TraineeModuleResponseDTO> getTraineeModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(traineeModuleService.getTraineeModuleById(id));
    }

    @GetMapping("/trainee-template/{traineeTemplateId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<List<TraineeModuleResponseDTO>> getTraineeModulesByTraineeTemplate(
            @PathVariable Long traineeTemplateId) {
        return ResponseEntity.ok(traineeModuleService.getTraineeModulesByTraineeTemplate(traineeTemplateId));
    }

    @GetMapping("/trainee-template/{traineeTemplateId}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<List<TraineeModuleResponseDTO>> getTraineeModulesByStatus(
            @PathVariable Long traineeTemplateId,
            @RequestParam ProgressStatus status) {
        return ResponseEntity.ok(traineeModuleService.getTraineeModulesByStatus(traineeTemplateId, status));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<TraineeModuleResponseDTO> updateTraineeModuleStatus(
            @PathVariable Long id,
            @RequestParam ProgressStatus status) {
        return ResponseEntity.ok(traineeModuleService.updateTraineeModuleStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteTraineeModule(@PathVariable Long id) {
        traineeModuleService.deleteTraineeModule(id);
        return ResponseEntity.ok("Trainee Module deleted successfully");
    }
}