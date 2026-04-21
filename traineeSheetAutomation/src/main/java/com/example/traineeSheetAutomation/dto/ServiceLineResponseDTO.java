package com.example.traineeSheetAutomation.dto;

import com.example.traineeSheetAutomation.entity.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceLineResponseDTO {

    private Long serviceLineId;
    private Department department;
}