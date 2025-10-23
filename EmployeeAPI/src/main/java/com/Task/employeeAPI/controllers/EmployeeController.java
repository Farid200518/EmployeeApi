package com.Task.employeeAPI.controllers;

import com.Task.employeeAPI.services.concrete.TaskService;
import com.Task.employeeAPI.dto.EmployeeDTO;
import com.Task.employeeAPI.dto.EmployeeLoginDTO;
import com.Task.employeeAPI.dto.TaskDTO;
import com.Task.employeeAPI.services.concrete.EmployeeService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//@Timed(value = "http.server.requests", extraTags = {"service", "EmployeeService"})
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final TaskService taskService;


    @PostMapping("/signup")
    public EmployeeDTO signup(@RequestBody @Valid EmployeeDTO employeeDTO) {
        return employeeService.signup(employeeDTO);
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid EmployeeLoginDTO userLoginDTO) {

        String token = employeeService.login(userLoginDTO);

        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return token;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER') or #id == authentication.principal.id")
    public EmployeeDTO getEmployeeById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id
    ) {
        return employeeService.findEmployeeById(id);
    }

    @Timed(value = "http.server.requests", extraTags = {"service", "EmployeeService"})
    @GetMapping
    @PreAuthorize("hasRole('HR') or hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public List<EmployeeDTO> getAllEmployee() {
        return employeeService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public EmployeeDTO updateEmployeeById(@PathVariable Integer id, @Valid @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.updateEmployeeById(id, employeeDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public EmployeeDTO deleteEmployeeById(@PathVariable Integer id) {
        return employeeService.deleteEmployeeById(id);
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER') or #id == authentication.principal.id")
    public List<TaskDTO> getAllEmployeeTasks(@PathVariable Integer id) {
        EmployeeDTO employee = employeeService.findEmployeeById(id);
        return taskService.findAllEmployeeTasks(employee.getId());
    }
}
