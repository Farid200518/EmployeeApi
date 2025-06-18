package com.Task.EmployeeAPI.Security;

import com.Task.EmployeeAPI.DAO.Entity.EmployeeEntity;
import com.Task.EmployeeAPI.DAO.Repository.EmployeeRepository;
import com.Task.EmployeeAPI.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        EmployeeEntity employeeEntity = employeeRepository.findByName(name);

        if (employeeEntity == null) {
            throw new NotFoundException("User " + name + " was not found!");
        }

        return new CustomUserDetails(
                employeeEntity.getId(),
                employeeEntity.getName(),
                employeeEntity.getPassword(),
                employeeEntity.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_" + employeeEntity.getRole().name())),
                employeeEntity.isDeleted()
                );
    }
}

