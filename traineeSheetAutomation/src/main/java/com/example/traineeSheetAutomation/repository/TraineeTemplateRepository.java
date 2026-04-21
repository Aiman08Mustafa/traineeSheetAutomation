package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.TraineeTemplate;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeTemplateRepository extends JpaRepository<TraineeTemplate, Long> {

    List<TraineeTemplate> findByTrainee_UserId(Long traineeId);

    List<TraineeTemplate> findByTemplate_TemplateId(Long templateId);

    List<TraineeTemplate> findByTrainee_UserIdAndStatus(Long traineeId, ProgressStatus status);

    boolean existsByTemplate_TemplateIdAndTrainee_UserId(Long templateId, Long traineeId);
}