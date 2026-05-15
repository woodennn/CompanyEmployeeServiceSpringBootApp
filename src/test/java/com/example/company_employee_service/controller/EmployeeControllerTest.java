package com.example.company_employee_service.controller;

import com.example.company_employee_service.model.Department;
import com.example.company_employee_service.model.Employee;
import com.example.company_employee_service.repository.DepartmentRepository;
import com.example.company_employee_service.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    public void testIndexPage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("html"));
    }

    @Test
    public void testAddDepartment() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "HR");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/department/add", request, String.class);

        assertEquals(302, response.getStatusCode().value());
        assertEquals(1, departmentRepository.count());
        assertEquals("HR", departmentRepository.findAll().get(0).getName());
    }

    @Test
    public void testEmployeeForm() {
        ResponseEntity<String> response = restTemplate.getForEntity("/employee/add", String.class);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("form"));
    }

    @Test
    public void testSaveEmployee() {
        Department department = new Department();
        department.setName("IT");
        department = departmentRepository.save(department);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("fullName", "John Doe");
        map.add("contactInfo", "+3800000000");
        map.add("address", "Kyiv");
        map.add("salary", "2500.0");
        map.add("department", String.valueOf(department.getId()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/employee/save", request, String.class);

        assertEquals(302, response.getStatusCode().value());
        assertEquals(1, employeeRepository.count());
        assertEquals("John Doe", employeeRepository.findAll().get(0).getFullName());
    }

    @Test
    public void testDeleteEmployee() {
        Department department = new Department();
        department.setName("Finance");
        department = departmentRepository.save(department);

        Employee employee = new Employee();
        employee.setFullName("Jane Doe");
        employee.setContactInfo("jane@test.com");
        employee.setAddress("Lviv");
        employee.setSalary(3000.0);
        employee.setDepartment(department);
        employee = employeeRepository.save(employee);

        Long id = employee.getId();

        // Робимо запит на видалення
        restTemplate.getForEntity("/employee/delete/" + id, String.class);

        // Перевіряємо головне: чи видалився запис із бази даних H2
        assertFalse(employeeRepository.existsById(id));
    }
}