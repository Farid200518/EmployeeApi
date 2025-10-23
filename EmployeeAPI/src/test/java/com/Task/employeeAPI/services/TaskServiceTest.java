package com.Task.employeeAPI.services;

import com.Task.employeeAPI.dao.Entity.Employee;
import com.Task.employeeAPI.dao.Entity.Task;
import com.Task.employeeAPI.dao.Entity.TaskWorkflow;
import com.Task.employeeAPI.dao.Enums.Priority;
import com.Task.employeeAPI.dao.Enums.Status;
import com.Task.employeeAPI.dao.Repository.EmployeeRepository;
import com.Task.employeeAPI.dao.Repository.TaskRepository;
import com.Task.employeeAPI.dao.Repository.TaskWorkflowRepository;
import com.Task.employeeAPI.dto.EmployeeDTO;
import com.Task.employeeAPI.dto.TaskDTO;
import com.Task.employeeAPI.exceptions.BadRequestException;
import com.Task.employeeAPI.exceptions.NotFoundException;
import com.Task.employeeAPI.security.CustomUserDetails;
import com.Task.employeeAPI.services.concrete.EmployeeService;
import com.Task.employeeAPI.services.concrete.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock private TaskRepository taskRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeeService employeeService;
    @Mock private TaskWorkflowRepository taskWorkflowRepository;
    @Mock private ModelMapper modelMapper;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // Helper to simulate authenticated user
    private void setAuth(String email, int userId) {
        CustomUserDetails user = new CustomUserDetails(
                userId, "u", "p", email,
                List.of(), true
        );
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(user, null));
    }

    @Test
    void createTask_nullDto_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> taskService.createTask(null));
    }

    @Test
    void createTask_noEmployeeId_throwsBadRequest() {
        TaskDTO dto = new TaskDTO();
        assertThrows(BadRequestException.class, () -> taskService.createTask(dto));
    }

    @Test
    void createTask_employeeNotFound_throwsNotFound() {
        TaskDTO dto = new TaskDTO();
        dto.setEmployeeId(5);
        when(employeeService.findEmployeeById(5))
                .thenThrow(new NotFoundException("no"));
        assertThrows(NotFoundException.class, () -> taskService.createTask(dto));
    }

    @Test
    void createTask_validDto_savesTaskAndWorkflowAndReturnsDto() {
        // Arrange DTO
        TaskDTO in = new TaskDTO();
        in.setEmployeeId(7);
        in.setDescription("foo");
        in.setPriority(Priority.MEDIUM);

        // map employee DTO → entity
        EmployeeDTO empDto = new EmployeeDTO();
        empDto.setId(7);
        when(employeeService.findEmployeeById(7)).thenReturn(empDto);
        Employee empEntity = new Employee(); empEntity.setId(7);
        when(modelMapper.map(empDto, Employee.class)).thenReturn(empEntity);

        // Authenticated updater
        setAuth("updater@example.com", 99);
        Employee updater = new Employee();
        updater.setEmail("updater@example.com");
        when(employeeRepository.findByEmailAndIsDeletedFalse("updater@example.com"))
                .thenReturn(updater);

        // map TaskDTO → Task
        Task taskEntity = new Task();
        when(modelMapper.map(in, Task.class)).thenReturn(taskEntity);

        // stub save
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        // stub workflow save
        TaskWorkflow tw = new TaskWorkflow();
        when(taskWorkflowRepository.save(any())).thenReturn(tw);

        // map back
        TaskDTO out = new TaskDTO();
        when(modelMapper.map(taskEntity, TaskDTO.class)).thenReturn(out);

        // Act
        TaskDTO result = taskService.createTask(in);

        // Assert
        assertSame(out, result);
        assertEquals(Status.CREATED, taskEntity.getStatus());
        verify(taskRepository).save(taskEntity);
        verify(taskWorkflowRepository).save(any(TaskWorkflow.class));
    }

    @Test
    void findTaskById_notFound_throwsNotFound() {
        when(taskRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taskService.findTaskById(1));
    }

    @Test
    void findTaskById_found_returnsDto() {
        Task t = new Task();
        when(taskRepository.findByIdAndIsDeletedFalse(2))
                .thenReturn(Optional.of(t));
        TaskDTO dto = new TaskDTO();
        when(modelMapper.map(t, TaskDTO.class)).thenReturn(dto);
        assertSame(dto, taskService.findTaskById(2));
    }

    @Test
    void findAll_returnsListOfDto() {
        Task t1 = new Task(), t2 = new Task();
        when(taskRepository.findByIsDeletedFalse())
                .thenReturn(List.of(t1, t2));
        TaskDTO d1 = new TaskDTO(), d2 = new TaskDTO();
        when(modelMapper.map(t1, TaskDTO.class)).thenReturn(d1);
        when(modelMapper.map(t2, TaskDTO.class)).thenReturn(d2);

        List<TaskDTO> list = taskService.findAll();
        assertEquals(2, list.size());
        assertTrue(list.containsAll(List.of(d1, d2)));
    }

    @Test
    void findAllEmployeeTasks_returnsOnlyThatEmployeeTasks() {
        Employee e = new Employee(); e.setId(10);
        when(employeeRepository.findByIdAndIsDeletedFalse(10)).thenReturn(Optional.of(e));

        Task t = new Task(); t.setEmployee(e);
        when(taskRepository.findByEmployee_idAndIsDeletedFalse(10))
                .thenReturn(List.of(t));

        TaskDTO d = new TaskDTO();
        when(modelMapper.map(t, TaskDTO.class)).thenReturn(d);

        List<TaskDTO> result = taskService.findAllEmployeeTasks(10);
        assertEquals(1, result.size());
        assertSame(d, result.get(0));
    }

    @Test
    void deleteTaskById_notFound_throwsNotFound() {
        when(taskRepository.findById(5)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taskService.deleteTaskById(5));
    }

    @Test
    void deleteTaskById_found_marksDeletedAndReturnsDto() {
        Task t = new Task();
        when(taskRepository.findById(6)).thenReturn(Optional.of(t));
        TaskDTO dto = new TaskDTO();
        when(modelMapper.map(t, TaskDTO.class)).thenReturn(dto);

        TaskDTO out = taskService.deleteTaskById(6);
        assertSame(dto, out);
        assertTrue(t.isDeleted());
        verify(taskRepository).save(t);
    }

    @Test
    void updateTaskById_nullDto_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> taskService.updateTaskById(1, null));
    }

    @Test
    void updateTaskById_notFound_throwsNotFound() {
        TaskDTO in = new TaskDTO();
        when(taskRepository.findByIdAndIsDeletedFalse(2))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taskService.updateTaskById(2, in));
    }

    @Test
    void updateTaskById_success_updatesFieldsAndReturnsDto() {
        // Arrange existing task
        Task existing = new Task();
        when(taskRepository.findByIdAndIsDeletedFalse(3))
                .thenReturn(Optional.of(existing));

        // EmployeeService returns DTO
        EmployeeDTO empDto = new EmployeeDTO();
        empDto.setId(20);
        when(employeeService.findEmployeeById(20)).thenReturn(empDto);
        Employee empEntity = new Employee(); empEntity.setId(20);
        when(modelMapper.map(empDto, Employee.class)).thenReturn(empEntity);

        // Prepare input DTO
        TaskDTO in = new TaskDTO();
        in.setEmployeeId(20);
        in.setDescription("new desc");
        in.setPriority(Priority.HIGH);

        // stub save and mapping
        when(modelMapper.map(existing, TaskDTO.class)).thenReturn(in);

        // Act
        TaskDTO out = taskService.updateTaskById(3, in);

        // Assert
        assertSame(in, out);
        assertEquals("new desc", existing.getDescription());
        assertEquals(Priority.HIGH, existing.getPriority());
        assertSame(empEntity, existing.getEmployee());
        verify(taskRepository).save(existing);
    }
}
