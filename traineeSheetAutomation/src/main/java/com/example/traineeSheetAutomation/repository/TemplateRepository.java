package com.example.traineeSheetAutomation.repository;

import com.example.traineeSheetAutomation.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByTitleIgnoreCase(String title);

    List<Template> findByCreatedBy_UserId(Long userId);

    boolean existsByTitleIgnoreCase(String title);
}
