package com.Task.employeeAPI.services;

import com.Task.employeeAPI.dao.Entity.Employee;
import com.Task.employeeAPI.dao.Enums.Role;
import com.Task.employeeAPI.dao.Repository.EmployeeRepository;
import com.Task.employeeAPI.dto.EmployeeDTO;
import com.Task.employeeAPI.dto.EmployeeLoginDTO;
import com.Task.employeeAPI.exceptions.BadCredentialsException;
import com.Task.employeeAPI.exceptions.BadRequestException;
import com.Task.employeeAPI.exceptions.NotFoundException;
import com.Task.employeeAPI.mapper.EmployeeMapper;
import com.Task.employeeAPI.security.JwtTokenUtil;
import com.Task.employeeAPI.services.concrete.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceTest.class);
    @InjectMocks
    private EmployeeService employeeService;

    @Mock private EmployeeRepository employeeRepository;
    @Mock private JwtTokenUtil jwtTokenUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private ModelMapper modelMapper;
    @Mock private EmployeeMapper employeeMapper;

    private EmployeeDTO dto;

    @Test
    void testSignupSuccess() {
        dto = new EmployeeDTO();
        dto.setEmail("test@example.com");
        dto.setName("Test");
        dto.setSurname("User");
        dto.setPassword("password");
        dto.setRole(Role.EMPLOYEE);

        when(employeeRepository.findByEmailAndIsDeletedFalse(dto.getEmail())).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(modelMapper.map(any(), eq(EmployeeDTO.class))).thenReturn(dto);

        Employee saved = Employee.builder()
                .name("Test")
                .surname("User")
                .email("test@example.com")
                .password("encoded")
                .role(Role.EMPLOYEE)
                .build();

        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeDTO result = employeeService.signup(dto);

        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void testSignupWithExistingEmail() {
        dto = new EmployeeDTO();
        dto.setEmail("test@example.com");

        when(employeeRepository.findByEmailAndIsDeletedFalse(dto.getEmail())).thenReturn(new Employee());

        assertThrows(BadRequestException.class, () -> employeeService.signup(dto));
    }

    @Test
    void testLoginSuccess() {
        EmployeeLoginDTO loginDTO = new EmployeeLoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("wrongpass");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenUtil.generateToken(loginDTO.getEmail())).thenReturn("token");

        String result = employeeService.login(loginDTO);

        assertEquals("token", result);
    }

    @Test
    void testLoginFailure() {
        EmployeeLoginDTO loginDTO = new EmployeeLoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("wrongpass");

        when(authenticationManager.authenticate(any())).thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        assertThrows(com.Task.employeeAPI.exceptions.BadCredentialsException.class, () -> employeeService.login(loginDTO));
    }

    @Test
    void testCreateEmployeeSuccess() {
        dto = new EmployeeDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");
        dto.setName("Test");
        dto.setSurname("User");
        dto.setRole(Role.EMPLOYEE);

        Employee mappedEmployee = new Employee();
        mappedEmployee.setPassword("pass");

        when(employeeRepository.findByEmailAndIsDeletedFalse(dto.getEmail())).thenReturn(null);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        when(modelMapper.map(any(EmployeeDTO.class), eq(Employee.class))).thenReturn(mappedEmployee);
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(dto);

        EmployeeDTO result = employeeService.createEmployee(dto);

        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void testCreateEmployeeWithNullDto() {
        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(null));
    }

    @Test
    void testCreateEmployeeDuplicateEmail() {
        dto = new EmployeeDTO();
        dto.setEmail("test@example.com");

        when(employeeRepository.findByEmailAndIsDeletedFalse(dto.getEmail())).thenReturn(new Employee());

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(dto));
    }

    @Test
    void testFindEmployeeByIdSuccess() {
        Employee employee = new Employee();
        employee.setId(1);

        dto = new EmployeeDTO();
        dto.setId(1);

        when(employeeMapper.findEmployeeById(1)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(dto);

        EmployeeDTO result = employeeService.findEmployeeById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void testFindEmployeeByIdNotFound() {
        when(employeeMapper.findEmployeeById(1)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> employeeService.findEmployeeById(1));
    }

    @Test
    void testFindAllEmployees() {
        List<Employee> employees = List.of(new Employee(), new Employee());
        when(employeeRepository.findByIsDeletedFalse()).thenReturn(employees);
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(new EmployeeDTO());

        List<EmployeeDTO> result = employeeService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testDeleteEmployeeByIdSuccess() {
        Employee employee = new Employee();
        employee.setId(1);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(new EmployeeDTO());

        EmployeeDTO result = employeeService.deleteEmployeeById(1);

        assertNotNull(result);
    }

    @Test
    void testDeleteEmployeeByIdNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.deleteEmployeeById(1));
    }

    @Test
    void testUpdateEmployeeSuccess() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("newpass");

        Employee employee = new Employee();
        employee.setId(1);
        employee.setEmail("old@example.com");

        when(employeeRepository.findByEmailAndIsDeletedFalse("new@example.com")).thenReturn(null);
        when(employeeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(dto);

        EmployeeDTO result = employeeService.updateEmployeeById(1, dto);

        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void testUpdateEmployeeEmailAlreadyExists() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("duplicate@example.com");

        Employee existing = new Employee();
        existing.setId(2);

        when(employeeRepository.findByEmailAndIsDeletedFalse("duplicate@example.com")).thenReturn(existing);

        assertThrows(BadRequestException.class, () -> employeeService.updateEmployeeById(1, dto));
    }

    @Test
    void testUpdateEmployeeNotFound() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("any@example.com");

        when(employeeRepository.findByEmailAndIsDeletedFalse("any@example.com")).thenReturn(null);
        when(employeeRepository.findByIdAndIsDeletedFalse(1)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> employeeService.updateEmployeeById(1, dto));
    }

    @Test
    void testUpdateEmployeeWithNullInput() {
        assertThrows(BadRequestException.class, () -> employeeService.updateEmployeeById(1, null));
    }
}
