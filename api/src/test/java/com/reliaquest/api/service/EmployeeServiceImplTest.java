package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class EmployeeServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    private EmployeeService employeeService;

    private static final String MOCK_API_URL = "http://localhost:8112/api/v1/employee";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeServiceImpl(restTemplate, MOCK_API_URL);
    }

    @Test
    public void testGetAllEmployees() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.singletonList(new Employee()));
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<Employee> employees = employeeService.getAllEmployees();
        assertEquals(1, employees.size());
    }

    @Test
    public void getAllEmployees_whenApiReturnsEmptyList_shouldReturnEmptyList() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.emptyList());
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<Employee> employees = employeeService.getAllEmployees();
        assertEquals(0, employees.size());
    }

    @Test
    public void getAllEmployees_whenApiFails_shouldThrowException() {
        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpClientErrorException.class, () -> {
            employeeService.getAllEmployees();
        });
    }

    @Test
    public void testGetEmployeeById() {
        ApiResponse<Employee> apiResponse = new ApiResponse<>();
        Employee employee = new Employee();
        employee.setId("1");
        apiResponse.setData(employee);
        ResponseEntity<ApiResponse<Employee>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(
                        eq(MOCK_API_URL + "/1"), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Employee result = employeeService.getEmployeeById("1");
        assertEquals("1", result.getId());
    }

    @Test
    public void getEmployeeById_whenNotFound_shouldReturnNull() {
        when(restTemplate.exchange(
                        eq(MOCK_API_URL + "/99"), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        Employee result = employeeService.getEmployeeById("99");
        assertNull(result);
    }


    @Test
    public void testGetEmployeesByNameSearch() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        Employee employee = new Employee();
        employee.setName("Tiger");
        apiResponse.setData(Collections.singletonList(employee));
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<Employee> employees = employeeService.getEmployeesByNameSearch("Tiger");
        assertEquals(1, employees.size());
        assertEquals("Tiger", employees.get(0).getName());
    }

    @Test
    public void getEmployeesByNameSearch_whenNoMatch_shouldReturnEmptyList() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        Employee employee = new Employee();
        employee.setName("Tiger");
        apiResponse.setData(Collections.singletonList(employee));
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<Employee> employees = employeeService.getEmployeesByNameSearch("Zebra");
        assertEquals(0, employees.size());
    }

    @Test
    public void testGetHighestSalaryOfEmployees() {
        Employee emp1 = new Employee();
        emp1.setSalary(1000);
        Employee emp2 = new Employee();
        emp2.setSalary(2000);
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        apiResponse.setData(List.of(emp1, emp2));
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertEquals(2000, highestSalary);
    }

    @Test
    public void getHighestSalaryOfEmployees_whenNoEmployees_shouldReturnZero() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        apiResponse.setData(Collections.emptyList());
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertEquals(0, highestSalary);
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() {
        Employee emp1 = new Employee();
        emp1.setName("John");
        emp1.setSalary(1000);
        Employee emp2 = new Employee();
        emp2.setName("Jane");
        emp2.setSalary(2000);
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        apiResponse.setData(List.of(emp1, emp2));
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(2, topEarners.size());
        assertEquals("Jane", topEarners.get(0));
        assertEquals("John", topEarners.get(1));
    }

    @Test
    public void testCreateEmployee() {
        EmployeeInput employeeInput = new EmployeeInput();
        employeeInput.setName("John Doe");
        employeeInput.setSalary(50000);
        employeeInput.setAge(30);

        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Doe");
        employee.setSalary(50000);
        employee.setAge(30);

        ApiResponse<Employee> apiResponse = new ApiResponse<>();
        apiResponse.setData(employee);
        ResponseEntity<ApiResponse<Employee>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        assertEquals("John Doe", createdEmployee.getName());
    }

    @Test
    public void createEmployee_whenApiFails_shouldThrowException() {
        EmployeeInput employeeInput = new EmployeeInput();
        employeeInput.setName("John Doe");

        when(restTemplate.exchange(eq(MOCK_API_URL), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.class, () -> {
            employeeService.createEmployee(employeeInput);
        });
    }

    @Test
    public void testDeleteEmployeeById() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("Test Employee");
        ApiResponse<Employee> getApiResponse = new ApiResponse<>();
        getApiResponse.setData(employee);
        ResponseEntity<ApiResponse<Employee>> getResponseEntity = ResponseEntity.ok(getApiResponse);

        when(restTemplate.exchange(
                eq(MOCK_API_URL + "/1"), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(getResponseEntity);

        ApiResponse<Boolean> deleteApiResponse = new ApiResponse<>();
        deleteApiResponse.setData(true);
        ResponseEntity<ApiResponse<Boolean>> deleteResponseEntity = ResponseEntity.ok(deleteApiResponse);

        when(restTemplate.exchange(
                eq(MOCK_API_URL), eq(HttpMethod.DELETE), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(deleteResponseEntity);

        String result = employeeService.deleteEmployeeById("1");
        assertEquals("Test Employee", result);
    }

    @Test
    public void deleteEmployeeById_whenApiFails_shouldThrowException() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("Test Employee");
        ApiResponse<Employee> getApiResponse = new ApiResponse<>();
        getApiResponse.setData(employee);
        ResponseEntity<ApiResponse<Employee>> getResponseEntity = ResponseEntity.ok(getApiResponse);

        when(restTemplate.exchange(
                eq(MOCK_API_URL + "/1"), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(getResponseEntity);

        when(restTemplate.exchange(
                eq(MOCK_API_URL), eq(HttpMethod.DELETE), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpClientErrorException.class, () -> {
            employeeService.deleteEmployeeById("1");
        });
    }

    @Test
    public void deleteEmployeeById_whenGetReturnsNotFound_shouldReturnNull() {
        when(restTemplate.exchange(
                eq(MOCK_API_URL + "/1"), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        String result = employeeService.deleteEmployeeById("1");
        assertNull(result);
    }

    @Test
    public void deleteEmployeeById_whenDeleteReturnsFalse_shouldReturnNull() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("Test Employee");
        ApiResponse<Employee> getApiResponse = new ApiResponse<>();
        getApiResponse.setData(employee);
        ResponseEntity<ApiResponse<Employee>> getResponseEntity = ResponseEntity.ok(getApiResponse);

        when(restTemplate.exchange(
                eq(MOCK_API_URL + "/1"), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(getResponseEntity);

        ApiResponse<Boolean> deleteApiResponse = new ApiResponse<>();
        deleteApiResponse.setData(false);
        ResponseEntity<ApiResponse<Boolean>> deleteResponseEntity = ResponseEntity.ok(deleteApiResponse);

        when(restTemplate.exchange(
                eq(MOCK_API_URL), eq(HttpMethod.DELETE), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(deleteResponseEntity);

        String result = employeeService.deleteEmployeeById("1");
        assertNull(result);
    }
}
