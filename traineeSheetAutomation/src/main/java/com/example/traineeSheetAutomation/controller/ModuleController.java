package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.ModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.ModuleResponseDTO;
import com.example.traineeSheetAutomation.service.ModuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@Tag(name = "Module APIs")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleResponseDTO> createModule(
            @Valid @RequestBody ModuleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.createModule(request));
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponseDTO>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<ModuleResponseDTO>> getModulesByTemplate(@PathVariable Long templateId) {
        return ResponseEntity.ok(moduleService.getModulesByTemplate(templateId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody ModuleRequestDTO request) {
        return ResponseEntity.ok(moduleService.updateModule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok("Module deleted successfully");
    }
}