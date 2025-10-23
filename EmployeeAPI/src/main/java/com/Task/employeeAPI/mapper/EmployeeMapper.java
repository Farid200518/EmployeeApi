package com.Task.employeeAPI.mapper;

import com.Task.employeeAPI.dao.Entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {
    @Select("SELECT * FROM employees WHERE id = #{id}")
    Employee findEmployeeById(Integer id);
}
