package com.Task.employeeAPI.dao.Entity;

import com.Task.employeeAPI.dao.Enums.Status;
import com.Task.employeeAPI.dao.Enums.Priority;
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

    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToMany(mappedBy = "task")
    private Set<TaskWorkflow> taskWorkflows= new HashSet<>();
}
