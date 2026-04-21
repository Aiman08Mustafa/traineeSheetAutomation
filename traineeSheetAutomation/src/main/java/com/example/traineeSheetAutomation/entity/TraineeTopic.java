package com.example.traineeSheetAutomation.entity;

import com.example.traineeSheetAutomation.entity.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trainee_topic")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainee_topic_id")
    private Long traineeTopicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_module_id", nullable = false)
    private TraineeModule traineeModule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProgressStatus status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "traineeTopic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UploadedContent> uploadedContents;

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