package com.Task.EmployeeAPI.Controllers;

import com.Task.EmployeeAPI.DTO.TaskDTO;
import com.Task.EmployeeAPI.DTO.TaskWorkflowDTO;
import com.Task.EmployeeAPI.Services.EmployeeService;
import com.Task.EmployeeAPI.Services.TaskService;
import com.Task.EmployeeAPI.Services.TaskWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final TaskWorkflowService taskWorkflowService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public TaskDTO getTaskById(@PathVariable Integer id) {
        return taskService.findTaskById(id);
    }

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

    @PostMapping("/{id}/take")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
    public TaskWorkflowDTO takeTaskWorkflow(Integer id, TaskWorkflowDTO taskWorkflowDTO){
        return taskWorkflowService.setToInProgress(id, taskWorkflowDTO);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR')")
    public TaskWorkflowDTO submitTaskWorkflow(Integer id, TaskWorkflowDTO taskWorkflowDTO){
        return taskWorkflowService.setToResolved(id, taskWorkflowDTO);
    }

    @PostMapping("{id}/confirm")
    @PreAuthorize("hasRole('HEAD_MANAGER')")
    public TaskWorkflowDTO confirmTaskWorkflow(@PathVariable Integer id, @Valid @RequestBody TaskWorkflowDTO taskWorkflowDTO) {
        return taskWorkflowService.setToDone(id, taskWorkflowDTO);
    }

    @PostMapping("{id}/return")
    @PreAuthorize("hasRole('HEAD_MANAGER')")
    public TaskWorkflowDTO returnTaskWorkflow(@PathVariable Integer id, @Valid @RequestBody TaskWorkflowDTO taskWorkflowDTO) {
        return taskWorkflowService.returnToInProgress(id, taskWorkflowDTO);
    }
}
