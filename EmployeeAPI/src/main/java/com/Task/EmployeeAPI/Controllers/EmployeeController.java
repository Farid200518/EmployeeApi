package com.Task.EmployeeAPI.Controllers;

import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import com.Task.EmployeeAPI.DTO.EmployeeLoginDTO;
import com.Task.EmployeeAPI.Services.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;


    @PostMapping("/signup")
    public ResponseEntity<EmployeeDTO> signup(@RequestBody @Valid EmployeeDTO employeeDTO) {
        EmployeeDTO created = employeeService.signup(employeeDTO);
        return ResponseEntity
                .ok()
                .body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid EmployeeLoginDTO userLoginDTO) {

        String token = employeeService.login(userLoginDTO);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: invalid credentials");
        }


        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER') or #id == authentication.principal.id")
    public ResponseEntity<EmployeeDTO> getEmployeeById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id
    ) {
        return ResponseEntity.ok(employeeService.findEmployeeById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('HR') or hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployee() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public ResponseEntity<EmployeeDTO> updateEmployeeById(@PathVariable Integer id, @Valid @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.updateEmployeeById(id, employeeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_MANAGER') or hasRole('HEAD_MANAGER')")
    public ResponseEntity<EmployeeDTO> deleteEmployeeById(@PathVariable Integer id) {
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }
}
