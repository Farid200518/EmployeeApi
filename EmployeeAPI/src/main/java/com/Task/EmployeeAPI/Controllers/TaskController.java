package com.Task.EmployeeAPI.Controllers;

import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import com.Task.EmployeeAPI.DTO.TaskDTO;
import com.Task.EmployeeAPI.Services.EmployeeService;
import com.Task.EmployeeAPI.Services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;

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

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER') or #id == authentication.principal.id")
    public List<TaskDTO> getAllEmployeeTasks(@PathVariable Integer id) {
        EmployeeDTO employee = employeeService.findEmployeeById(id);
        List<TaskDTO> tasks = new ArrayList<>(taskService.findAll());
        return tasks.stream()
                .filter(task -> task.getEmployeeId() == employee.getId())
                .toList();
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
}
