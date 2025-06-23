package com.Task.EmployeeAPI.DAO.Repository;

import com.Task.EmployeeAPI.DAO.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    Employee findByName(String name);
    List<Employee> findByIsDeletedFalse();
    Employee findByEmailAndIsDeletedFalse(String email);
    Employee findByIdAndIsDeletedFalse(Integer id);

}
