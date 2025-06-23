package com.Task.EmployeeAPI.DAO.Repository;

import com.Task.EmployeeAPI.DAO.Entity.TaskWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskWorkflowRepository extends JpaRepository<TaskWorkflow, Integer> {

}
