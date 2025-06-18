package com.Task.EmployeeAPI.Services;

import com.Task.EmployeeAPI.DAO.Entity.EmployeeEntity;
import com.Task.EmployeeAPI.DAO.Entity.TaskEntity;
import com.Task.EmployeeAPI.DAO.Repository.TaskRepository;
import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import com.Task.EmployeeAPI.DTO.TaskDTO;
import com.Task.EmployeeAPI.Exceptions.BadRequestException;
import com.Task.EmployeeAPI.Exceptions.NotFoundException;
import com.Task.EmployeeAPI.Mappers.EmployeeMapper;
import com.Task.EmployeeAPI.Mappers.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService{

    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;
    private final TaskMapper taskMapper;
    private final EmployeeMapper employeeMapper;


    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        if (taskDTO == null)
            throw new BadRequestException("Task input must not be null!");

        if (taskDTO.getEmployeeId() == null)
            throw new BadRequestException("Employee ID must not be null!");

        EmployeeDTO employeeDTO = employeeService.findEmployeeById(taskDTO.getEmployeeId());
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employeeDTO);

        if (employeeEntity.isDeleted()) throw new NotFoundException("You cannot assign task to employee with ID " + employeeEntity.getId() + " , because employee was deleted!");

        TaskEntity taskEntity = taskMapper.toEntity(taskDTO);
        taskEntity.setEmployee(employeeEntity);
        taskEntity = taskRepository.save(taskEntity);
        return taskMapper.toDto(taskEntity);
    }

    @Override
    public TaskDTO findTaskById(Integer id) {
        TaskEntity taskEntity = taskRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Task with ID " + id + " doesn't exist!"));

        if (taskEntity.isDeleted()) throw new BadRequestException("Task with ID " + id + "  is deleted!");

        return taskMapper.toDto(taskEntity);
    }

    @Override
    public List<TaskDTO> findAll() {
        return taskRepository
                .findAll()
                .stream()
                .filter(task -> !task.isDeleted())
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskDTO deleteTaskById(Integer id) {
        TaskEntity taskEntity = taskRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Task with ID " + id + " doesn't exist!"));

//        if (taskEntity.isDeleted()) {
//            throw new BadRequestException("Task with ID " + id + " is already deleted!");
//        }

        taskEntity.setDeleted(true);
        taskRepository.save(taskEntity);
        return taskMapper.toDto(taskEntity);
    }

    @Override
    public TaskDTO updateTaskById(Integer id, TaskDTO taskDTO) {
        if (taskDTO == null) {
            throw new BadRequestException("Employee input must not be null");
        }

        TaskEntity taskEntity = taskRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Task with ID " + id + " doesn't exist!"));

        if (taskEntity.isDeleted()) {
            throw new NotFoundException("Task with ID " + id + " is deleted!");
        }

        EmployeeDTO employeeDTO = employeeService.findEmployeeById(taskDTO.getEmployeeId());
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employeeDTO);

        taskEntity.setDescription(taskDTO.getDescription());
        taskEntity.setPriority(taskDTO.getPriority());
        taskEntity.setEmployee(employeeEntity);
        taskRepository.save(taskEntity);
        return taskMapper.toDto(taskEntity);
    }
}
