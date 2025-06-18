package com.Task.EmployeeAPI.Mappers;

import com.Task.EmployeeAPI.DAO.Entity.EmployeeEntity;
import com.Task.EmployeeAPI.DAO.Entity.TaskEntity;
import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import com.Task.EmployeeAPI.DTO.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "employee", ignore = true)
    TaskEntity toEntity(TaskDTO taskDTO);

    @Mapping(source = "employee.id", target = "employeeId")
    TaskDTO toDto(TaskEntity taskEntity);
}
