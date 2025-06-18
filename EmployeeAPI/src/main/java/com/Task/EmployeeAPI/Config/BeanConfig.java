package com.Task.EmployeeAPI.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
