package com.Task.employeeAPI.services.abstraction;

import com.Task.employeeAPI.dto.EmployeeDTO;
import com.Task.employeeAPI.dto.EmployeeLoginDTO;

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
