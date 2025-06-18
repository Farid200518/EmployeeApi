package com.Task.EmployeeAPI.Services;

import com.Task.EmployeeAPI.DAO.Entity.EmployeeEntity;
import com.Task.EmployeeAPI.DAO.Repository.EmployeeRepository;
import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import com.Task.EmployeeAPI.DTO.EmployeeLoginDTO;
import com.Task.EmployeeAPI.Exceptions.BadCredentialsException;
import com.Task.EmployeeAPI.Exceptions.BadRequestException;
import com.Task.EmployeeAPI.Exceptions.NotFoundException;
import com.Task.EmployeeAPI.Mappers.EmployeeMapper;
import com.Task.EmployeeAPI.Security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService{

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeDTO signup(EmployeeDTO employeeDTO) {
        EmployeeEntity existingByEmail = employeeRepository.findByEmail(employeeDTO.getEmail());
        if (existingByEmail != null && !existingByEmail.isDeleted()) {
            throw new BadRequestException("Email already exists");
        }

        EmployeeEntity existingByUsername = employeeRepository.findByName(employeeDTO.getName());
        if (existingByUsername != null && !existingByUsername.isDeleted()) {
            throw new BadRequestException("Username already exists");
        }

        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .name(employeeDTO.getName())
                .surname(employeeDTO.getSurname())
                .email(employeeDTO.getEmail())
                .password(passwordEncoder.encode(employeeDTO.getPassword()))
                .role(employeeDTO.getRole())
                .build();

        employeeRepository.save(employeeEntity);
        return employeeMapper.toDto(employeeEntity);
    }

    @Override
    public String login(EmployeeLoginDTO employeeLoginDTO) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            employeeLoginDTO.getName(),
                            employeeLoginDTO.getPassword()
                    )
            );
            return jwtTokenUtil.generateToken(employeeLoginDTO.getName());
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }


    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            throw new BadRequestException("Employee input must not be null");
        }

        EmployeeEntity existingByEmail = employeeRepository.findByEmail(employeeDTO.getEmail());
        if (existingByEmail != null && !existingByEmail.isDeleted()) {
            throw new BadRequestException("Email already exists");
        }

        EmployeeEntity existingByUsername = employeeRepository.findByName(employeeDTO.getName());
        if (existingByUsername != null && !existingByUsername.isDeleted()) {
            throw new BadRequestException("Username already exists");
        }

        EmployeeEntity employeeEntity = employeeMapper.toEntity(employeeDTO);
        employeeEntity.setPassword(passwordEncoder.encode(employeeEntity.getPassword()));
        employeeRepository.save(employeeEntity);
        return employeeMapper.toDto(employeeEntity);
    }

    @Override
    public EmployeeDTO findEmployeeById(Integer id) {
        EmployeeEntity employeeEntity = employeeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with ID " + id + " doesn't exist!"));

        if (employeeEntity.isDeleted()) {
            throw new BadRequestException("Employee with ID " + id + " is deleted!");
        }

        return modelMapper.map(employeeEntity, EmployeeDTO.class);
//        return employeeMapper.toDto(employeeEntity);
    }

    @Override
    public List<EmployeeDTO> findAll() {
        return employeeRepository
                .findByDeletedFalse()
                .stream()
//                .filter(employee -> !employee.isDeleted())
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public EmployeeDTO deleteEmployeeById(Integer id) {
        EmployeeEntity employeeEntity = employeeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with  ID " + id + " doesn't exist!"));

        if (employeeEntity.isDeleted()) {
            throw new NotFoundException("Employee with ID " + id + " is already deleted!");
        }

        employeeEntity.setDeleted(true);
        employeeRepository.save(employeeEntity);
        return employeeMapper.toDto(employeeEntity);
    }

    @Override
    public EmployeeDTO updateEmployeeById(Integer id, EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            throw new BadRequestException("Employee input must not be null");
        }

        EmployeeEntity existingByEmail = employeeRepository.findByEmail(employeeDTO.getEmail());

        if (existingByEmail != null && existingByEmail.getId() != id) {
            throw new BadRequestException("Email already exists");
        }

        EmployeeEntity existingByUsername = employeeRepository.findByName(employeeDTO.getName());

        if (existingByUsername != null && existingByUsername.getId() != id) {
            throw new BadRequestException("Username already exists");
        }


        EmployeeEntity employeeEntity = employeeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with ID " + id + " doesn't exist!"));

        if (employeeEntity.isDeleted()) {
            throw new BadRequestException("Employee with ID " + id + " is deleted!");
        }

        employeeEntity.setName(employeeDTO.getName());
        employeeEntity.setSurname(employeeDTO.getSurname());
        employeeEntity.setEmail(employeeDTO.getEmail());
        employeeEntity.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employeeEntity.setRole(employeeDTO.getRole());
        employeeRepository.save(employeeEntity);
        return employeeMapper.toDto(employeeEntity);
    }
}
