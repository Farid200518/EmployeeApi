package com.Task.EmployeeAPI.DAO.Entity;

import com.Task.EmployeeAPI.DAO.Enums.Priority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;
}
