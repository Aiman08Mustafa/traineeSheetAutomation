package com.example.traineeSheetAutomation.entity;

import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trainee_module")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainee_module_id")
    private Long traineeModuleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainee_template_id", nullable = false)
    private TraineeTemplate traineeTemplate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProgressStatus status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "traineeModule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TraineeTopic> traineeTopics;

    @PrePersist
    protected void onCreate() {
        this.status = ProgressStatus.NOT_STARTED;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == ProgressStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}