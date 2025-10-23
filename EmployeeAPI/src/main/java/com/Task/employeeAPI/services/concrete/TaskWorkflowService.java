package com.Task.employeeAPI.services.concrete;

import com.Task.employeeAPI.dao.Enums.Status;
import com.Task.employeeAPI.dao.Repository.TaskWorkflowRepository;
import com.Task.employeeAPI.dto.NotificationDTO;
import com.Task.employeeAPI.exceptions.BadRequestException;
import com.Task.employeeAPI.exceptions.NotFoundException;
import com.Task.employeeAPI.dao.Entity.Employee;
import com.Task.employeeAPI.dao.Entity.Task;
import com.Task.employeeAPI.dao.Entity.TaskWorkflow;
import com.Task.employeeAPI.dao.Repository.EmployeeRepository;
import com.Task.employeeAPI.dao.Repository.TaskRepository;
import com.Task.employeeAPI.dto.TaskWorkflowDTO;
//import com.Task.employeeAPI.notification.NotificationProducer;
import com.Task.employeeAPI.security.CustomUserDetails;
import com.Task.employeeAPI.payload.TaskWorkflowPayload;
import com.Task.employeeAPI.services.abstraction.ITaskWorkflowService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskWorkflowService implements ITaskWorkflowService {
    private final TaskWorkflowRepository taskWorkflowRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final ModelMapper modelMapper;
//    private final NotificationProducer notificationProducer;

    @Override
    public List<TaskWorkflowDTO> getAllWorkflowsByTaskId(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Task with id " + id + " was not found!"));

        boolean isHead = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_HEAD_MANAGER"));

        boolean isHR = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_HR_MANAGER"));

        boolean isOwner = task.getEmployee().getId() == userDetails.getId();

        if (!(isHead || isHR || isOwner)) {
            throw new BadRequestException("You are not authorized to view this task's workflows.");
        }

        // Map and return
        return taskWorkflowRepository.findByTask_Id(id)
                .stream()
                .map(taskWorkflow -> modelMapper.map(taskWorkflow, TaskWorkflowDTO.class))
                .toList();
    }


    @Override
    public TaskWorkflowPayload setStatus(TaskWorkflowPayload taskWorkflowPayload) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskWorkflowPayload.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task with id " + taskWorkflowPayload.getTaskId() + " was not found!"));
        Status currentStatus = task.getStatus();
        Status requestedStatus = taskWorkflowPayload.getStatus();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        boolean isHead = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_HEAD_MANAGER"));

        if (requestedStatus == Status.CREATED) {
            throw new BadRequestException("Cannot set task to status CREATED!");
        }

        if (!isHead && task.getEmployee().getId() != userDetails.getId()) {
            throw new BadRequestException("Cannot manipulate another user's task!");
        }

        if (requestedStatus == Status.IN_PROGRESS) {
            if (currentStatus == Status.CREATED && !isHead) {
                // Employee starting work — allowed
            } else if (currentStatus == Status.RESOLVED && isHead) {

                String recipient = task.getEmployee().getEmail();
                if (recipient == null || recipient.isBlank()) {
                    throw new BadRequestException("Cannot send notification—no recipient email on task " + task.getId());
                }

//                notificationProducer.sendNotification(new NotificationDTO(
//                        task.getEmployee().getEmail(),
//                        "Task reviewed",
//                        "Your task has been marked as IN_PROGRESS by your manager."
//                ));

            } else {
                throw new BadRequestException("Invalid transition to IN_PROGRESS.");
            }
        }

        if (requestedStatus == Status.RESOLVED) {
            if (currentStatus != Status.IN_PROGRESS) {
                throw new BadRequestException("Task must be IN_PROGRESS to be set to RESOLVED.");
            }
            if (isHead) {
                throw new BadRequestException("Only employees can set task to RESOLVED, heads cannot.");
            }
        }


        if (requestedStatus == Status.DONE) {
            if (currentStatus != Status.RESOLVED) {
                throw new BadRequestException("Task must be RESOLVED to be set to DONE.");
            }
            if (!isHead) {
                throw new BadRequestException("Only HEAD_MANAGER can mark task as DONE.");
            }

            String recipient = task.getEmployee().getEmail();
            if (recipient == null || recipient.isBlank()) {
                throw new BadRequestException("Cannot send notification—no recipient email on task " + task.getId());
            }

//            notificationProducer.sendNotification(new NotificationDTO(
//                    recipient,
//                    "Task reviewed",
//                    "Your task has been marked as DONE by your manager."
//            ));
        }

        Employee employee = employeeRepository.findByEmailAndIsDeletedFalse(userDetails.getEmail());

        TaskWorkflow updatedTaskWorkflow = new TaskWorkflow();
        updatedTaskWorkflow.setStatus(requestedStatus);
        updatedTaskWorkflow.setLastUpdated(LocalDateTime.now());
        updatedTaskWorkflow.setUpdatedBy(employee);
        updatedTaskWorkflow.setTask(task);
        taskWorkflowRepository.save(updatedTaskWorkflow);

        task.setStatus(requestedStatus);

        taskRepository.save(task);

        return taskWorkflowPayload;
    }

}
