package com.Task.employeeAPI.services.concrete;

import com.Task.employeeAPI.exceptions.BadRequestException;
import com.Task.employeeAPI.exceptions.NotFoundException;
import com.Task.employeeAPI.security.JwtTokenUtil;
import com.Task.employeeAPI.dao.Entity.Employee;
import com.Task.employeeAPI.dao.Repository.EmployeeRepository;
import com.Task.employeeAPI.dto.EmployeeDTO;
import com.Task.employeeAPI.dto.EmployeeLoginDTO;
import com.Task.employeeAPI.mapper.EmployeeMapper;
import com.Task.employeeAPI.services.abstraction.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository employeeRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeDTO signup(EmployeeDTO employeeDTO) {
        Employee existingByEmail = employeeRepository.findByEmailAndIsDeletedFalse(employeeDTO.getEmail());
        if (existingByEmail != null) {
            throw new BadRequestException("Email already exists");
        }

        Employee employee = Employee.builder()
                .name(employeeDTO.getName())
                .surname(employeeDTO.getSurname())
                .email(employeeDTO.getEmail())
                .password(passwordEncoder.encode(employeeDTO.getPassword()))
                .role(employeeDTO.getRole())
                .build();

        employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public String login(EmployeeLoginDTO employeeLoginDTO) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            employeeLoginDTO.getEmail(),
                            employeeLoginDTO.getPassword()
                    )
            );
            return jwtTokenUtil.generateToken(employeeLoginDTO.getEmail());

        } catch (AuthenticationException ex) {
            throw new com.Task.employeeAPI.exceptions.BadCredentialsException("Invalid email or password");
        }
    }


    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            throw new BadRequestException("Employee input must not be null");
        }

        Employee existingByEmail = employeeRepository.findByEmailAndIsDeletedFalse(employeeDTO.getEmail());

        if (existingByEmail != null) {
            throw new BadRequestException("Email or username already exists");
        }

        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO findEmployeeById(Integer id) {
        Employee employee = employeeMapper.findEmployeeById(id);

        if (employee == null) {
            throw new NotFoundException("Employee with ID " + id + " was not found!");
        }

        return modelMapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public List<EmployeeDTO> findAll() {
        return employeeRepository
                .findByIsDeletedFalse()
                .stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .toList();
    }

    @Override
    public EmployeeDTO deleteEmployeeById(Integer id) {
        Employee employee = employeeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with  ID " + id + " doesn't exist!"));

        employee.setDeleted(true);
        employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployeeById(Integer id, EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            throw new BadRequestException("Employee input must not be null");
        }

        Employee existingByEmail = employeeRepository.findByEmailAndIsDeletedFalse(employeeDTO.getEmail());

        if (existingByEmail != null && existingByEmail.getId() != id) {
            throw new BadRequestException("Email already exists");
        }


        Employee employee = employeeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Employee with  ID " + id + " doesn't exist!"));


        employee.setName(employeeDTO.getName());
        employee.setSurname(employeeDTO.getSurname());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employee.setRole(employeeDTO.getRole());
        employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeDTO.class);
    }
}
