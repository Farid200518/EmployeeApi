package com.Task.EmployeeAPI.Security;

import com.Task.EmployeeAPI.DAO.Entity.Employee;
import com.Task.EmployeeAPI.DAO.Repository.EmployeeRepository;
import com.Task.EmployeeAPI.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmailAndIsDeletedFalse(email);

        if (employee == null) {
            throw new NotFoundException("User " + email + " was not found!");
        }

        return new CustomUserDetails(
                employee.getId(),
                employee.getName(),
                employee.getPassword(),
                employee.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().name())),
                employee.isDeleted()
                );
    }

//    public CustomUserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
//        Employee employee = employeeRepository.findByEmailAndIsDeletedFalse(email);
//
//        if (employee == null) {
//            throw new NotFoundException("User was not found!");
//        }
//
//        return new CustomUserDetails(
//                employee.getId(),
//                employee.getName(),
//                employee.getPassword(),
//                employee.getEmail(),
//                List.of(new SimpleGrantedAuthority(employee.getRole().name())),
//                employee.isDeleted()
//        );
//    }
}

