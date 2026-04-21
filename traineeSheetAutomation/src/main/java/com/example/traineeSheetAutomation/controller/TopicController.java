package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.ModuleRequestDTO;
import com.example.traineeSheetAutomation.dto.ModuleResponseDTO;
import com.example.traineeSheetAutomation.dto.TopicRequestDTO;
import com.example.traineeSheetAutomation.dto.TopicResponseDTO;
import com.example.traineeSheetAutomation.service.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "Topic APIs")
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TopicResponseDTO> createTopic(
            @Valid @RequestBody TopicRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.createTopic(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<List<TopicResponseDTO>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<TopicResponseDTO> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getTopicById(id));
    }

    @GetMapping("/module/{moduleId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TRAINEE')")
    public ResponseEntity<List<TopicResponseDTO>> getTopicByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(topicService.getTopicByModule(moduleId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TopicResponseDTO> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequestDTO request) {
        return ResponseEntity.ok(topicService.updateTopic(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok("Topic deleted successfully");
    }

}
