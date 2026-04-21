package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByTemplate_TemplateIdOrderBySequenceOrderAsc(Long templateId);

    boolean existsByTemplate_TemplateIdAndSequenceOrder(Long templateId, Integer sequenceOrder);
}