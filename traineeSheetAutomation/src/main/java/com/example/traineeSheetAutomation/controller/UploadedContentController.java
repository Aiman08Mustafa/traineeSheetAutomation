package com.example.traineeSheetAutomation.controller;

import com.example.traineeSheetAutomation.dto.UploadedContentRequestDTO;
import com.example.traineeSheetAutomation.dto.UploadedContentResponseDTO;
import com.example.traineeSheetAutomation.service.UploadedContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/uploaded-contents")
@RequiredArgsConstructor
@Tag(name = "Uploaded Content APIs")
public class UploadedContentController {

    private final UploadedContentService uploadedContentService;

    @PostMapping
    public ResponseEntity<UploadedContentResponseDTO> uploadContent(
            @Valid @RequestBody UploadedContentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedContentService.uploadContent(request));
    }

    @GetMapping
    public ResponseEntity<List<UploadedContentResponseDTO>> getAllUploadedContents() {
        return ResponseEntity.ok(uploadedContentService.getAllUploadedContents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UploadedContentResponseDTO> getUploadedContentById(@PathVariable Long id) {
        return ResponseEntity.ok(uploadedContentService.getUploadedContentById(id));
    }

    @GetMapping("/trainee-topic/{traineeTopicId}")
    public ResponseEntity<List<UploadedContentResponseDTO>> getUploadedContentsByTraineeTopic(
            @PathVariable Long traineeTopicId) {
        return ResponseEntity.ok(uploadedContentService.getUploadedContentsByTraineeTopic(traineeTopicId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UploadedContentResponseDTO> updateUploadedContent(
            @PathVariable Long id,
            @Valid @RequestBody UploadedContentRequestDTO request) {
        return ResponseEntity.ok(uploadedContentService.updateUploadedContent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUploadedContent(@PathVariable Long id) {
        uploadedContentService.deleteUploadedContent(id);
        return ResponseEntity.ok("Uploaded Content deleted successfully");
    }
}