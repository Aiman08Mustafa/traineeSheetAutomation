package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.TraineeTopic;
import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeTopicRepository extends JpaRepository<TraineeTopic, Long> {

    List<TraineeTopic> findByTraineeModule_TraineeModuleId(Long traineeModuleId);

    List<TraineeTopic> findByTraineeModule_TraineeModuleIdAndStatus(Long traineeModuleId, ProgressStatus status);

    List<TraineeTopic> findByTopic_TopicId(Long topicId);

    boolean existsByTraineeModule_TraineeModuleIdAndTopic_TopicId(Long traineeModuleId, Long topicId);
}