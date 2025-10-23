package com.Task.employeeAPI.dao.Repository;

import com.Task.employeeAPI.dao.Entity.TaskWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskWorkflowRepository extends JpaRepository<TaskWorkflow, Integer> {
    List<TaskWorkflow> findByTask_Id(Integer id);
}
