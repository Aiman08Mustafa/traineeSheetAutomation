package com.example.traineeSheetAutomation.dto;

import com.example.traineeSheetAutomation.entity.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {

    private Long roleId;
    private RoleName roleName;
}