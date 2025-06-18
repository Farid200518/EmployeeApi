package com.Task.EmployeeAPI.DAO.Repository;

import com.Task.EmployeeAPI.DAO.Entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    EmployeeEntity findByName(String name);
    EmployeeEntity findByEmail(String email);
    List<EmployeeEntity> findByDeletedFalse();

}
