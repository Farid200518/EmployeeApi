package com.Task.employeeAPI.services;

import com.Task.employeeAPI.dao.Entity.Employee;
import com.Task.employeeAPI.dao.Entity.TaskWorkflow;
import com.Task.employeeAPI.dao.Enums.Status;
import com.Task.employeeAPI.dao.Repository.TaskWorkflowRepository;
import com.Task.employeeAPI.dto.TaskWorkflowDTO;
import com.Task.employeeAPI.exceptions.BadRequestException;
import com.Task.employeeAPI.exceptions.NotFoundException;
import com.Task.employeeAPI.payload.TaskWorkflowPayload;
import com.Task.employeeAPI.security.CustomUserDetails;
import com.Task.employeeAPI.dao.Entity.Task;
import com.Task.employeeAPI.dao.Repository.EmployeeRepository;
import com.Task.employeeAPI.dao.Repository.TaskRepository;
import com.Task.employeeAPI.dto.TaskDTO;
import com.Task.employeeAPI.services.concrete.TaskService;
import com.Task.employeeAPI.services.concrete.TaskWorkflowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskWorkflowServiceTest {

    @InjectMocks
    private TaskWorkflowService taskWorkflowService;

    @Mock private TaskWorkflowRepository taskWorkflowRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private TaskService taskService;
    @Mock private ModelMapper modelMapper;

    private TaskWorkflowPayload payload;
    private Task task;
    private TaskDTO taskDTO;
    private Employee employee;
    private TaskWorkflow taskWorkflow;
    private TaskWorkflowDTO taskWorkflowDTO;

    private void setAuthWithRole(String role, int userId) {
        CustomUserDetails userDetails = new CustomUserDetails(
                userId,
                "Farid",
                "password",
                "farid@example.com",
                Collections.singleton(new SimpleGrantedAuthority(role)),
                true
        );
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(userDetails, null)
        );
    }

    // ----------------------------   Tests for setStatus method   ---------------------------------
    @Test
    void testCreatedToInProgress_ByOwner_ShouldPass() {
        // Arrange
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        payload = new TaskWorkflowPayload();
        payload.setStatus(Status.IN_PROGRESS);
        payload.setTaskId(1);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setStatus(Status.CREATED); task.setEmployee(employee);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.CREATED);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);
        when(employeeRepository.findByEmailAndIsDeletedFalse(any())).thenReturn(employee);

        // Act & Assert
        TaskWorkflowPayload result = taskWorkflowService.setStatus(payload);
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void testCreatedToResolved_ByOwner_ShouldPass() {
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.IN_PROGRESS);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.IN_PROGRESS);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.RESOLVED);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);
        when(employeeRepository.findByEmailAndIsDeletedFalse(any())).thenReturn(employee);

        TaskWorkflowPayload result = taskWorkflowService.setStatus(payload);
        assertEquals(Status.RESOLVED, result.getStatus());
    }

    @Test
    void testResolvedToInProgress_ByHeadManager_ShouldPass() {
        setAuthWithRole("ROLE_HEAD_MANAGER", 999);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.RESOLVED);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.RESOLVED);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.IN_PROGRESS);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);
        when(employeeRepository.findByEmailAndIsDeletedFalse(any())).thenReturn(employee);

        TaskWorkflowPayload result = taskWorkflowService.setStatus(payload);
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void testResolvedToDone_ByHeadManager_ShouldPass() {
        setAuthWithRole("ROLE_HEAD_MANAGER", 999);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.RESOLVED);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.RESOLVED);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.DONE);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);
        when(employeeRepository.findByEmailAndIsDeletedFalse(any())).thenReturn(employee);

        TaskWorkflowPayload result = taskWorkflowService.setStatus(payload);
        assertEquals(Status.DONE, result.getStatus());
    }

    @Test
    void testCreatedToResolved_ShouldFail() {
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.CREATED);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.CREATED);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.RESOLVED);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);

        assertThrows(BadRequestException.class, () -> taskWorkflowService.setStatus(payload));
    }

    @Test
    void testSetStatusCreated_ShouldFail() {
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.IN_PROGRESS);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.IN_PROGRESS);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.CREATED);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);

        assertThrows(BadRequestException.class, () -> taskWorkflowService.setStatus(payload));
    }

    @Test
    void testSetStatusByNonOwnerNonHead_ShouldFail() {
        setAuthWithRole("ROLE_EMPLOYEE", 999); // not owner

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.IN_PROGRESS);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.IN_PROGRESS);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.RESOLVED);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);

        assertThrows(BadRequestException.class, () -> taskWorkflowService.setStatus(payload));
    }

    @Test
    void testSetDoneByEmployee_ShouldFail() {
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.RESOLVED);

        taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.RESOLVED);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.DONE);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);

        assertThrows(BadRequestException.class, () -> taskWorkflowService.setStatus(payload));
    }

    @Test
    void testSetResolvedInvalidCurrentStatus_ShouldFail() {
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        employee = new Employee(); employee.setId(100);
        task = new Task(); task.setId(1); task.setEmployee(employee); task.setStatus(Status.CREATED);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1); taskDTO.setEmployeeId(100); taskDTO.setStatus(Status.CREATED);

        payload = new TaskWorkflowPayload(); payload.setTaskId(1); payload.setStatus(Status.RESOLVED);

        when(taskService.findTaskById(1)).thenReturn(taskDTO);
        when(modelMapper.map(any(TaskDTO.class), eq(Task.class))).thenReturn(task);

        assertThrows(BadRequestException.class, () -> taskWorkflowService.setStatus(payload));
    }

    // ----------------------------   Tests for getAllTaskWorkflowsByTaskId method   ---------------------------------
    @Test
    void testTaskNotFound_ShouldTrowNotFoundException() {
        setAuthWithRole("ROLE_EMPLOYEE", 1);

        when(taskRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskWorkflowService.getAllWorkflowsByTaskId(1));
    }

    @Test
    void testUserIsTheOwner_ShouldReturnWorkflows() {
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        employee = new Employee();
        employee.setId(100);

        task = new Task();
        task.setId(1);
        task.setEmployee(employee);

        when(taskRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(task));
        when(taskWorkflowRepository.findByTask_Id(1)).thenReturn(Collections.emptyList());

        List<TaskWorkflowDTO> result = taskWorkflowService.getAllWorkflowsByTaskId(1);

        assertNotNull(result);
    }

    @Test
    void testUserIsHead_ShouldReturnWorkflows() {
        setAuthWithRole("ROLE_HEAD_MANAGER", 200);

        employee = new Employee();
        employee.setId(100);

        task = new Task();
        task.setId(1);
        task.setEmployee(employee);

        when(taskRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(task));
        when(taskWorkflowRepository.findByTask_Id(1)).thenReturn(Collections.emptyList());

        List<TaskWorkflowDTO> result = taskWorkflowService.getAllWorkflowsByTaskId(1);

        assertNotNull(result);
    }

    @Test
    void testUserIsHRManager_ShouldReturnWorkflows() {
        setAuthWithRole("ROLE_HR_MANAGER", 200);

        employee = new Employee();
        employee.setId(100);

        task = new Task();
        task.setId(1);
        task.setEmployee(employee);

        when(taskRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(task));
        when(taskWorkflowRepository.findByTask_Id(1)).thenReturn(Collections.emptyList());

        List<TaskWorkflowDTO> result = taskWorkflowService.getAllWorkflowsByTaskId(1);

        assertNotNull(result);
    }

    @Test
    void testUserIsNotOwnerNorManager_ShouldThrowBadRequestException() {
        setAuthWithRole("ROLE_EMPLOYEE", 200);

        employee = new Employee();
        employee.setId(100);

        task = new Task();
        task.setId(1);
        task.setEmployee(employee);

        when(taskRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(task));

        assertThrows(BadRequestException.class, () -> taskWorkflowService.getAllWorkflowsByTaskId(1));
    }

    @Test
    void testReturnDTOsProperly_ShouldReturnMappedDTOs() {
        setAuthWithRole("ROLE_EMPLOYEE", 100);

        employee = new Employee();
        employee.setId(100);

        task = new Task();
        task.setId(1);
        task.setEmployee(employee);

        taskWorkflow = new TaskWorkflow();
        taskWorkflow.setTask(task);
        taskWorkflow.setStatus(Status.IN_PROGRESS);

        when(taskRepository.findByIdAndIsDeletedFalse(1)).thenReturn(Optional.of(task));
        when(taskWorkflowRepository.findByTask_Id(1)).thenReturn(Collections.singletonList(taskWorkflow));

        taskWorkflowDTO = new TaskWorkflowDTO();
        when(modelMapper.map(taskWorkflow, TaskWorkflowDTO.class)).thenReturn(taskWorkflowDTO);

        List<TaskWorkflowDTO> result = taskWorkflowService.getAllWorkflowsByTaskId(1);

        assertEquals(1, result.size());
        assertEquals(taskWorkflowDTO, result.getFirst());
    }
}
