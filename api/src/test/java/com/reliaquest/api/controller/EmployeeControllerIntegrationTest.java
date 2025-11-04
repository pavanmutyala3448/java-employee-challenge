package com.reliaquest.api.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import java.util.Collections;

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

        given()
                .when()
                .get("/api/v1/employees")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].employee_name", equalTo("John Doe"));
    }
}