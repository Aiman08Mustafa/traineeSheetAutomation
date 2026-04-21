package com.example.traineeSheetAutomation.entity;

import com.example.traineeSheetAutomation.entity.enums.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ServiceLine")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceLineID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @OneToMany(mappedBy = "serviceLine")
    private List<Template> templates;


}
