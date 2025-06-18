package com.Task.EmployeeAPI.Mappers;

import com.Task.EmployeeAPI.DAO.Entity.EmployeeEntity;
import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    EmployeeEntity toEntity(EmployeeDTO dto);

    EmployeeDTO toDto(EmployeeEntity entity);
}
