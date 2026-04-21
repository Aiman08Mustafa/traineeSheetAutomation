package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.ServiceLineRequestDTO;
import com.example.traineeSheetAutomation.dto.ServiceLineResponseDTO;
import com.example.traineeSheetAutomation.service.ServiceLineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-lines")
@RequiredArgsConstructor
@Tag(name = "Service Line APIs")
public class ServiceLineController {

    private final ServiceLineService serviceLineService;

    @PostMapping
    public ResponseEntity<ServiceLineResponseDTO> createServiceLine(
            @Valid @RequestBody ServiceLineRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceLineService.createServiceLine(request));
    }

    @GetMapping
    public ResponseEntity<List<ServiceLineResponseDTO>> getAllServiceLines() {
        return ResponseEntity.ok(serviceLineService.getAllServiceLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceLineResponseDTO> getServiceLineById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceLineService.getServiceLineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceLineResponseDTO> updateServiceLine(
            @PathVariable Long id,
            @Valid @RequestBody ServiceLineRequestDTO request) {
        return ResponseEntity.ok(serviceLineService.updateServiceLine(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServiceLine(@PathVariable Long id) {
        serviceLineService.deleteServiceLine(id);
        return ResponseEntity.ok("Service Line deleted successfully");
    }
}