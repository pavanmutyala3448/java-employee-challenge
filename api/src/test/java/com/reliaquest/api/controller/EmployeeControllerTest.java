package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEmployees() {
        when(employeeService.getAllEmployees()).thenReturn(Collections.singletonList(new Employee()));
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetEmployeeById() {
        Employee employee = new Employee();
        employee.setId("1d6f3419-6bc9-4090-9f5c-64328d0dfe5a");
        when(employeeService.getEmployeeById("1d6f3419-6bc9-4090-9f5c-64328d0dfe5a"))
                .thenReturn(employee);
        ResponseEntity<Employee> response = employeeController.getEmployeeById("1d6f3419-6bc9-4090-9f5c-64328d0dfe5a");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("1d6f3419-6bc9-4090-9f5c-64328d0dfe5a", response.getBody().getId());
    }

    @Test
    public void getEmployeeById_whenEmployeeNotFound_shouldReturnNotFound() {
        String validUuid = UUID.randomUUID().toString();
        when(employeeService.getEmployeeById(validUuid)).thenReturn(null);

        ResponseEntity<Employee> response = employeeController.getEmployeeById(validUuid);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testGetHighestSalaryOfEmployees() {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(100000);
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(100000, response.getBody());
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() {
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(Collections.singletonList("John Doe"));
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetEmployeesByNameSearch() {
        when(employeeService.getEmployeesByNameSearch("John")).thenReturn(Collections.singletonList(new Employee()));
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch("John");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testCreateEmployee() {
        EmployeeInput employeeInput = new EmployeeInput();
        Employee employee = new Employee();
        when(employeeService.createEmployee(employeeInput)).thenReturn(employee);
        ResponseEntity<Employee> response = employeeController.createEmployee(employeeInput);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteEmployee() {
        when(employeeService.deleteEmployeeById("1d6f3419-6bc9-4090-9f5c-64328d0dfe5a"))
                .thenReturn("Success");
        ResponseEntity<String> response = employeeController.deleteEmployeeById("1d6f3419-6bc9-4090-9f5c-64328d0dfe5a");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success", response.getBody());
    }

    @Test
    public void deleteEmployeeById_whenEmployeeNotFound_shouldReturnNotFound() {
        String validUuid = UUID.randomUUID().toString();
        when(employeeService.deleteEmployeeById(validUuid)).thenReturn(null);

        ResponseEntity<String> response = employeeController.deleteEmployeeById(validUuid);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
