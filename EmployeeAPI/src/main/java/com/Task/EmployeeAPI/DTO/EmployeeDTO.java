package com.Task.EmployeeAPI.DTO;

import com.Task.EmployeeAPI.DAO.Enums.Role;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeDTO {

    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 15, message = "Name is too long!")
    @Pattern(regexp = "^[A-Za-z'\\s-]+$", message = "Name must contain only letters")
    private String name;

    @NotBlank(message = "Surname cannot be blank")
    @Size(max = 15, message = "Surname is too long!")
    @Pattern(regexp = "^[A-Za-z'\\s-]+$", message = "Surname must contain only letters")
    private String surname;

    @NotBlank
    @Email(message = "Must be Valid email address")
    private String email;

    @Size(min = 8)
    @NotBlank(message = "password must be not blank")
    private String password;


    private Role role;
}
