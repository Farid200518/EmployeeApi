package com.Task.EmployeeAPI.Services;

import com.Task.EmployeeAPI.DAO.Entity.Employee;
import com.Task.EmployeeAPI.DAO.Entity.TaskWorkflow;
import com.Task.EmployeeAPI.DAO.Enums.Status;
import com.Task.EmployeeAPI.DAO.Repository.EmployeeRepository;
import com.Task.EmployeeAPI.DAO.Repository.TaskWorkflowRepository;
import com.Task.EmployeeAPI.DTO.TaskWorkflowDTO;
import com.Task.EmployeeAPI.Exceptions.BadRequestException;
import com.Task.EmployeeAPI.Exceptions.NotFoundException;
import com.Task.EmployeeAPI.Security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskWorkflowService implements ITaskWorkflowService {
    private final TaskWorkflowRepository taskWorkflowRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;


    @Override
    public TaskWorkflowDTO setToInProgress(Integer id, TaskWorkflowDTO taskWorkflowDTO) {
        TaskWorkflow taskWorkflow = taskWorkflowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not Found!"));

        if (taskWorkflow.getStatus() != Status.CREATED) {
            throw new BadRequestException("Employee can only change status to IN_PROGRESS if status is CREATED");
        }

        if (taskWorkflowDTO.getStatus() != Status.IN_PROGRESS) {
            throw new BadRequestException("Employee can only set status to IN_PROGRESS!");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Employee employee = employeeRepository.findByEmailAndIsDeletedFalse(userDetails.getEmail());

        TaskWorkflow updatedTaskWorkflow = new TaskWorkflow();
        updatedTaskWorkflow.setStatus(taskWorkflowDTO.getStatus());
        updatedTaskWorkflow.setLastUpdated(LocalDateTime.now());
        updatedTaskWorkflow.setUpdatedBy(employee);
        taskWorkflowRepository.save(updatedTaskWorkflow);
        return modelMapper.map(updatedTaskWorkflow, TaskWorkflowDTO.class);
    }

    @Override
    public TaskWorkflowDTO setToResolved(Integer id, TaskWorkflowDTO taskWorkflowDTO) {
        TaskWorkflow taskWorkflow = taskWorkflowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not Found!"));

        if (taskWorkflow.getStatus() != Status.IN_PROGRESS) {
            throw new BadRequestException("Employee can only change status to RESOLVED if status is IN_PROGRESS");
        }

        if (taskWorkflowDTO.getStatus() != Status.RESOLVED) {
            throw new BadRequestException("Employee can only set status to RESOLVED!");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Employee employee = employeeRepository.findByEmailAndIsDeletedFalse(userDetails.getEmail());

        TaskWorkflow updatedTaskWorkflow = new TaskWorkflow();
        updatedTaskWorkflow.setStatus(taskWorkflowDTO.getStatus());
        updatedTaskWorkflow.setLastUpdated(LocalDateTime.now());
        updatedTaskWorkflow.setUpdatedBy(employee);
        taskWorkflowRepository.save(updatedTaskWorkflow);
        return modelMapper.map(updatedTaskWorkflow, TaskWorkflowDTO.class);
    }

    @Override
    public TaskWorkflowDTO returnToInProgress(Integer id, TaskWorkflowDTO taskWorkflowDTO) {
        TaskWorkflow taskWorkflow = taskWorkflowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not Found!"));

        if (taskWorkflow.getStatus() != Status.RESOLVED) {
            throw new BadRequestException("Head can only change status to IN_PROGRESS if status is RESOLVED");
        }

        if (taskWorkflowDTO.getStatus() != Status.IN_PROGRESS) {
            throw new BadRequestException("Head can only set status to IN_PROGRESS!");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Employee employee = employeeRepository.findByEmailAndIsDeletedFalse(userDetails.getEmail());

        TaskWorkflow updatedTaskWorkflow = new TaskWorkflow();
        updatedTaskWorkflow.setStatus(taskWorkflowDTO.getStatus());
        updatedTaskWorkflow.setLastUpdated(LocalDateTime.now());
        updatedTaskWorkflow.setUpdatedBy(employee);

        return modelMapper.map(updatedTaskWorkflow, TaskWorkflowDTO.class);
    }


    @Override
    public TaskWorkflowDTO setToDone(Integer id, TaskWorkflowDTO taskWorkflowDTO) {
        TaskWorkflow taskWorkflow = taskWorkflowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not Found!"));

        if (taskWorkflow.getStatus() != Status.RESOLVED) {
            throw new BadRequestException("Head can only change status to IN_PROGRESS if status is RESOLVED");
        }

        if (taskWorkflowDTO.getStatus() != Status.DONE) {
            throw new BadRequestException("Head can only set status to DONE!");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Employee employee = employeeRepository.findByEmailAndIsDeletedFalse(userDetails.getEmail());

        TaskWorkflow updatedTaskWorkflow = new TaskWorkflow();
        updatedTaskWorkflow.setStatus(taskWorkflowDTO.getStatus());
        updatedTaskWorkflow.setLastUpdated(LocalDateTime.now());
        updatedTaskWorkflow.setUpdatedBy(employee);

        return modelMapper.map(updatedTaskWorkflow, TaskWorkflowDTO.class);
    }

}
