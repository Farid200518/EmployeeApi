package com.Task.employeeAPI.dao.Entity;

import com.Task.employeeAPI.dao.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@Entity
@Table(name = "task_workflow")
public class TaskWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    @LastModifiedDate
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee updatedBy;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
