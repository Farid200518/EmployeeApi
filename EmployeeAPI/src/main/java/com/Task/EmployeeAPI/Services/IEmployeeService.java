package com.Task.EmployeeAPI.Services;

import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import com.Task.EmployeeAPI.DTO.EmployeeLoginDTO;

import java.util.List;


public interface IEmployeeService {
    EmployeeDTO signup(EmployeeDTO employeeDTO);
    String login(EmployeeLoginDTO employeeLoginDTO);
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO findEmployeeById(Integer id);
    List<EmployeeDTO> findAll();
    EmployeeDTO deleteEmployeeById(Integer id);
    EmployeeDTO updateEmployeeById(Integer id, EmployeeDTO employeeDTO);
}
