package com.reliaquest.api.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EmployeeControllerResilienceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static WireMockServer wireMockServer;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public RestTemplate testRestTemplate() {
            return new RestTemplateBuilder().rootUri(wireMockServer.baseUrl()).build();
        }
    }

    @DynamicPropertySource
    static void overrideWebClientBaseUrl(DynamicPropertyRegistry registry) {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        registry.add("mock.api.url", wireMockServer::baseUrl);
    }

    @BeforeAll
    static void startWireMock() {
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @ParameterizedTest
    @ValueSource(ints = {429, 503})
    void testGetAllEmployeesWithRetry(int statusCode) {
        // First 2 calls fail, 3rd succeeds
        wireMockServer.stubFor(get(urlEqualTo("/"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse().withStatus(statusCode))
                .willSetStateTo("First Attempt Failed"));

        wireMockServer.stubFor(get(urlEqualTo("/"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("First Attempt Failed")
                .willReturn(aResponse().withStatus(statusCode))
                .willSetStateTo("Second Attempt Failed"));

        String employeeId = UUID.randomUUID().toString();
        String employeeName = "John Doe";
        int employeeSalary = 50000;
        int employeeAge = 30;

        String successResponse = String.format(
                "{\"status\":\"success\",\"data\":[{\"id\":\"%s\",\"employee_name\":\"%s\",\"employee_salary\":%d,\"employee_age\":%d,\"profile_image\":\"\"}]}",
                employeeId, employeeName, employeeSalary, employeeAge);

        wireMockServer.stubFor(get(urlEqualTo("/"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Second Attempt Failed")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(successResponse)));

        ResponseEntity<List<Employee>> response = testRestTemplate.exchange(
                "/api/v1/employees",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(employeeName, response.getBody().get(0).getName());

        wireMockServer.verify(3, getRequestedFor(urlEqualTo("/")));
    }
}