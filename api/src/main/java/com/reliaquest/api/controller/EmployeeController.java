package com.reliaquest.api.controller;

import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;
    private static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final String INVALID_UUID_MESSAGE = "Invalid UUID format";

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Received request to get all employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("Received request to search employees by name with string: {}", searchString);
        if (searchString == null || searchString.isBlank()) {
            throw new BadRequestException("Search string cannot be empty");
        }
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("Received request to get employee by id: {}", id);
        if (!UUID_REGEX.matcher(id).matches()) {
            throw new BadRequestException(INVALID_UUID_MESSAGE);
        }
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        logger.warn("Employee with id {} not found", id);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Received request to get highest salary of employees");
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Received request to get top ten highest earning employee names");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeInput employeeInput) {
        logger.info("Received request to create employee: {}", employeeInput);
        return ResponseEntity.ok(employeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        logger.info("Received request to delete employee by id: {}", id);
        if (!UUID_REGEX.matcher(id).matches()) {
            throw new BadRequestException(INVALID_UUID_MESSAGE);
        }
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        if (deletedEmployeeName != null) {
            return ResponseEntity.ok(deletedEmployeeName);
        }
        logger.warn("Could not delete employee with id {}, as it was not found", id);
        return ResponseEntity.notFound().build();
    }
}
