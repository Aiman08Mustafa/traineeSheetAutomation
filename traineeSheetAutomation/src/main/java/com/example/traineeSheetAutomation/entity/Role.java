package com.example.traineeSheetAutomation.entity;

import com.example.traineeSheetAutomation.entity.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName roleName;

    @OneToMany(mappedBy = "role")
    private List<User> users;



}