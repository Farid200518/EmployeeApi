package com.Task.EmployeeAPI.DAO.Repository;

import com.Task.EmployeeAPI.DAO.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Task findByIdAndIsDeletedFalse(Integer id);
    List<Task> findByIsDeletedFalse();
    List<Task> findByEmployee_idAndIsDeletedFalse(Integer id);
}
