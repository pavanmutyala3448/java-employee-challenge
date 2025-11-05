package com.reliaquest.api.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void getAllEmployees_shouldReturnEmployees() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Doe");
        when(employeeService.getAllEmployees()).thenReturn(Collections.singletonList(employee));

        given().when()
                .get("/api/v1/employees")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].employee_name", equalTo("John Doe"));
    }

    @Test
    public void getEmployeesByNameSearch_shouldReturnMatchingEmployees() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Doe");
        when(employeeService.getEmployeesByNameSearch("John")).thenReturn(Collections.singletonList(employee));

        given().when()
                .get("/api/v1/employees/search/John")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].employee_name", equalTo("John Doe"));
    }

    @Test
    public void getEmployeeById_shouldReturnEmployee() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Doe");
        String validUuid = UUID.randomUUID().toString();
        when(employeeService.getEmployeeById(validUuid)).thenReturn(employee);

        given().when()
                .get("/api/v1/employees/" + validUuid)
                .then()
                .statusCode(200)
                .body("employee_name", equalTo("John Doe"));
    }

    @Test
    public void getHighestSalaryOfEmployees_shouldReturnHighestSalary() {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(100000);

        given().when()
                .get("/api/v1/employees/highestSalary")
                .then()
                .statusCode(200)
                .body(equalTo("100000"));
    }

    @Test
    public void getTopTenHighestEarningEmployeeNames_shouldReturnNames() {
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(Arrays.asList("John Doe", "Jane Doe"));

        given().when()
                .get("/api/v1/employees/topTenHighestEarningEmployeeNames")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0]", equalTo("John Doe"));
    }

    @Test
    public void createEmployee_shouldReturnCreatedEmployee() {
        EmployeeInput employeeInput = new EmployeeInput();
        employeeInput.setName("John Doe");
        employeeInput.setSalary(50000);
        employeeInput.setAge(30);
        employeeInput.setTitle("Teacher");

        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Doe");
        employee.setSalary(50000);
        employee.setAge(30);
        employee.setTitle("Teacher");

        when(employeeService.createEmployee(any(EmployeeInput.class))).thenReturn(employee);

        given().contentType(ContentType.JSON)
                .body(employeeInput)
                .when()
                .post("/api/v1/employees")
                .then()
                .statusCode(200)
                .body("employee_name", equalTo("John Doe"));
    }

    @Test
    public void deleteEmployeeById_shouldReturnSuccessMessage() {
        String validUuid = UUID.randomUUID().toString();
        when(employeeService.deleteEmployeeById(validUuid)).thenReturn("Successfully! deleted Record");

        given().when()
                .delete("/api/v1/employees/" + validUuid)
                .then()
                .statusCode(200)
                .body(equalTo("Successfully! deleted Record"));
    }
}
