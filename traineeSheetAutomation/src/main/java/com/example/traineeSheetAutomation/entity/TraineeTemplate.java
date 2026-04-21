package com.example.traineeSheetAutomation.entity;

import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trainee_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainee_template_id")
    private Long traineeTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProgressStatus status;

    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "traineeTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TraineeModule> traineeModules;

    @PrePersist
    protected void onCreate() {
        this.status = ProgressStatus.NOT_STARTED;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}