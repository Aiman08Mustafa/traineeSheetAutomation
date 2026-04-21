package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.ServiceLine;
import com.example.traineeSheetAutomation.entity.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceLineRepository extends JpaRepository<ServiceLine, Long> {
    Optional<ServiceLine> findByDepartment(Department department);

    boolean existsByDepartment(Department department);
}
