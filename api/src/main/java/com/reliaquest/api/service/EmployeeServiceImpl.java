package com.reliaquest.api.service;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final RestTemplate restTemplate;
    private final String mockApiUrl;

    public EmployeeServiceImpl(RestTemplate restTemplate, @Value("${mock.api.url}") String mockApiUrl) {
        this.restTemplate = restTemplate;
        this.mockApiUrl = mockApiUrl;
    }

    @Override
    @Retryable(value = HttpClientErrorException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Employee> getAllEmployees() {
        logger.info("Attempting to fetch all employees");
        try {
            ResponseEntity<ApiResponse<List<Employee>>> response = restTemplate.exchange(
                    mockApiUrl, HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {});
            List<Employee> employees = Objects.nonNull(response.getBody()) ? response.getBody().getData() : Collections.emptyList();
            logger.info("Successfully fetched {} employees", employees.size());
            return employees;
        } catch (HttpClientErrorException e) {
            logger.error("Error while fetching all employees", e);
            throw e;
        }
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        logger.info("Searching employees with name: {}", searchString);
        return getAllEmployees().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .toList();
    }

    @Override
    @Retryable(value = HttpClientErrorException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Employee getEmployeeById(String id) {
        logger.info("Attempting to fetch employee with id: {}", id);
        try {
            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                    mockApiUrl + "/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {});
            Employee employee = Objects.nonNull(response.getBody()) ? response.getBody().getData() : null;
            if (employee != null) {
                logger.info("Successfully fetched employee with id: {}", id);
            } else {
                logger.warn("No employee found with id: {}", id);
            }
            return employee;
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Employee with id {} not found in remote API", id);
            return null;
        } catch (HttpClientErrorException e) {
            logger.error("Error while fetching employee with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        logger.info("Calculating highest salary of all employees");
        return getAllEmployees().stream()
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching top 10 highest earning employee names");
        return getAllEmployees().stream()
                .sorted((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .toList();
    }

    @Override
    public Employee createEmployee(EmployeeInput employeeInput) {
        logger.info("Attempting to create employee: {}", employeeInput);
        try {
            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                    mockApiUrl,
                    HttpMethod.POST,
                    new org.springframework.http.HttpEntity<>(employeeInput),
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {});
            Employee newEmployee = response.getBody() != null ? response.getBody().getData() : null;
            logger.info("Successfully created employee: {}", newEmployee);
            return newEmployee;
        } catch (HttpClientErrorException e) {
            logger.error("Error while creating employee: {}", employeeInput, e);
            throw e;
        }
    }

    @Override
    public String deleteEmployeeById(String id) {
        logger.info("Attempting to delete employee with id: {}", id);
        try {
            String url = mockApiUrl + "/" + id;
            ResponseEntity<ApiResponse<String>> response =
                    restTemplate.exchange(url, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});
            String deletedEmployeeName = response.getBody() != null ? response.getBody().getData() : null;
            logger.info("Successfully deleted employee with id: {}", id);
            return deletedEmployeeName;
        } catch (HttpClientErrorException e) {
            logger.error("Error while deleting employee with id: {}", id, e);
            throw e;
        }
    }
}
