package com.Task.EmployeeAPI.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeLoginDTO {

    @NotBlank(message = "Username mustn't be blank")
    private String name;

    @Size(min = 8)
    @NotBlank(message = "Password mustn't be blank")
    private String password;
}
