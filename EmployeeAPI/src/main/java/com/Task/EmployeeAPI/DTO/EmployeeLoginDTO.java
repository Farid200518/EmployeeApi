package com.Task.EmployeeAPI.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeLoginDTO {

    @Email
    @NotBlank(message = "Email mustn't be blank")
    private String email;

    @Size(min = 8)
    @NotBlank(message = "Password mustn't be blank")
    private String password;
}
