package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.TraineeTopicRequestDTO;
import com.example.traineeSheetAutomation.dto.TraineeTopicResponseDTO;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import com.example.traineeSheetAutomation.service.TraineeTopicService;
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
@RequestMapping("/api/v1/trainee-topics")
@RequiredArgsConstructor
@Tag(name = "Trainee Topic APIs")
public class TraineeTopicController {

    private final TraineeTopicService traineeTopicService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create trainee topic (Manager only)")
    public ResponseEntity<TraineeTopicResponseDTO> createTraineeTopic(
            @Valid @RequestBody TraineeTopicRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeTopicService.createTraineeTopic(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get all trainee topics (Manager only)")
    public ResponseEntity<List<TraineeTopicResponseDTO>> getAllTraineeTopics() {
        return ResponseEntity.ok(traineeTopicService.getAllTraineeTopics());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    @Operation(summary = "Get trainee topic by ID (Manager and Trainee)")
    public ResponseEntity<TraineeTopicResponseDTO> getTraineeTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(traineeTopicService.getTraineeTopicById(id));
    }

    @GetMapping("/trainee-module/{traineeModuleId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    @Operation(summary = "Get trainee topics by trainee module (Manager and Trainee)")
    public ResponseEntity<List<TraineeTopicResponseDTO>> getTraineeTopicsByTraineeModule(
            @PathVariable Long traineeModuleId) {
        return ResponseEntity.ok(traineeTopicService.getTraineeTopicsByTraineeModule(traineeModuleId));
    }

    @GetMapping("/trainee-module/{traineeModuleId}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    @Operation(summary = "Get trainee topics by status (Manager and Trainee)")
    public ResponseEntity<List<TraineeTopicResponseDTO>> getTraineeTopicsByStatus(
            @PathVariable Long traineeModuleId,
            @RequestParam ProgressStatus status) {
        return ResponseEntity.ok(traineeTopicService.getTraineeTopicsByStatus(traineeModuleId, status));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    @Operation(summary = "Update trainee topic status (Manager and Trainee)")
    public ResponseEntity<TraineeTopicResponseDTO> updateTraineeTopicStatus(
            @PathVariable Long id,
            @RequestParam ProgressStatus status) {
        return ResponseEntity.ok(traineeTopicService.updateTraineeTopicStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete trainee topic (Manager only)")
    public ResponseEntity<String> deleteTraineeTopic(@PathVariable Long id) {
        traineeTopicService.deleteTraineeTopic(id);
        return ResponseEntity.ok("Trainee Topic deleted successfully");
    }
}