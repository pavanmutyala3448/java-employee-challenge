package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.Collections;
import java.util.List;
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
        employee.setId("1");
        when(employeeService.getEmployeeById("1")).thenReturn(employee);
        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("1", response.getBody().getId());
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
        when(employeeService.deleteEmployeeById("1")).thenReturn("Success");
        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Success", response.getBody());
    }
}
