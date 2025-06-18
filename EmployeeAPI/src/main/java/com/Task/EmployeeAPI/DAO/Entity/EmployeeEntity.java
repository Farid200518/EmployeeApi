package com.Task.EmployeeAPI.DAO.Entity;

import com.Task.EmployeeAPI.DAO.Enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String surname;

    @Builder.Default
    private boolean isDeleted = false;

    @Column(nullable = false)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "employee")
    @Builder.Default
    private Set<TaskEntity> tasks = new HashSet<>();
}