package com.Task.employeeAPI.dao.Repository;

import com.Task.employeeAPI.dao.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findByIdAndIsDeletedFalse(Integer id);
    List<Task> findByIsDeletedFalse();
    List<Task> findByEmployee_idAndIsDeletedFalse(Integer id);
}
