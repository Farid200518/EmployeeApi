package com.Task.EmployeeAPI.DAO.Repository;

import com.Task.EmployeeAPI.DAO.Entity.EmployeeEntity;
import com.Task.EmployeeAPI.DAO.Entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    List<TaskEntity> findByEmployee_IdAndDeletedFalse(EmployeeEntity employeeEntity);
}
