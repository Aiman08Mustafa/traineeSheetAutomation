package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.UploadedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedContentRepository extends JpaRepository<UploadedContent, Long> {

    List<UploadedContent> findByTraineeTopic_TraineeTopicId(Long traineeTopicId);

    boolean existsByTraineeTopic_TraineeTopicIdAndFileName(Long traineeTopicId, String fileName);
}