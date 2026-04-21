package com.example.traineeSheetAutomation.dto;

import com.example.traineeSheetAutomation.entity.enums.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {

    @NotNull(message = "Role name is required")
    private RoleName roleName;
}