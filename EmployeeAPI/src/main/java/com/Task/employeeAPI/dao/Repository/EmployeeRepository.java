package com.Task.employeeAPI.dao.Repository;

import com.Task.employeeAPI.dao.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByIsDeletedFalse();
    Employee findByEmailAndIsDeletedFalse(String email);
    Optional<Employee> findByIdAndIsDeletedFalse(Integer id);

}
