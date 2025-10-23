package com.Task.employeeAPI.controllers;

import com.Task.employeeAPI.dto.TaskWorkflowDTO;
import com.Task.employeeAPI.services.concrete.TaskService;
import com.Task.employeeAPI.services.concrete.TaskWorkflowService;
import com.Task.employeeAPI.dto.TaskDTO;
import com.Task.employeeAPI.payload.TaskWorkflowPayload;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Timed(value = "http.server.requests", extraTags = {"service", "TaskService"})
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskWorkflowService taskWorkflowService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public TaskDTO getTaskById(@PathVariable Integer id) {
        return taskService.findTaskById(id);
    }

    @Timed(value = "http.server.requests", extraTags = {"service", "EmployeeService"})
    @GetMapping
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public List<TaskDTO> getAllTask() {
        return taskService.findAll();
    }


    @PostMapping
    @PreAuthorize("hasRole('HEAD_MANAGER')")
    public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {

        return taskService.createTask(taskDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HEAD_MANAGER')")
    public TaskDTO deleteTaskById(@PathVariable Integer id) {
        return taskService.deleteTaskById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HEAD_MANAGER')")
    public TaskDTO updateTaskById(@PathVariable Integer id, @Valid @RequestBody TaskDTO taskDTO) {
        return taskService.updateTaskById(id, taskDTO);
    }

    @GetMapping("{id}/workflows")
    @PreAuthorize("hasRole('HEAD_MANAGER') or hasRole('HR_MANAGER') or hasRole('EMPLOYEE') ")
    public List<TaskWorkflowDTO> getWorkflowsOfTheTask(@PathVariable Integer id) {
        return taskWorkflowService.getAllWorkflowsByTaskId(id);
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('HEAD_MANAGER') or hasRole('HR_MANAGER') or hasRole('EMPLOYEE')")
    public TaskWorkflowPayload changeStatus(@Valid @RequestBody TaskWorkflowPayload payload){
        return taskWorkflowService.setStatus(payload);
    }
}
