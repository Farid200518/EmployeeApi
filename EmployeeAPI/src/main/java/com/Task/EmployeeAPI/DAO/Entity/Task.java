package com.Task.EmployeeAPI.DAO.Entity;

import com.Task.EmployeeAPI.DAO.Enums.Priority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToMany(mappedBy = "task")
    private Set<TaskWorkflow> taskWorkflows= new HashSet<>();
}
