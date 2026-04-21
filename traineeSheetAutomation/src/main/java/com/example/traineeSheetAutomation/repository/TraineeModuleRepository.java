package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.TraineeModule;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeModuleRepository extends JpaRepository<TraineeModule, Long> {

    List<TraineeModule> findByTraineeTemplate_TraineeTemplateId(Long traineeTemplateId);

    List<TraineeModule> findByTraineeTemplate_TraineeTemplateIdAndStatus(Long traineeTemplateId, ProgressStatus status);

    List<TraineeModule> findByModule_ModuleId(Long moduleId);

    Optional<TraineeModule> findByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(Long traineeTemplateId, Long moduleId);

    boolean existsByTraineeTemplate_TraineeTemplateIdAndModule_ModuleId(Long traineeTemplateId, Long moduleId);
}