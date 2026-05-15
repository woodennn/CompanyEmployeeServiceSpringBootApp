package com.example.company_employee_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String contactInfo;
    private String address;
    private Double salary;

    // Зв'язок: багато співробітників належать до одного департаменту
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}