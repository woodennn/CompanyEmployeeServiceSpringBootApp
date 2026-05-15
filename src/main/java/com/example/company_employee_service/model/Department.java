package com.example.company_employee_service.model;

import jakarta.persistence.*;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Геттер та сеттер для ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        // На випадок якщо ідентифікатор присвоюється базою даних
        this.id = id;
    }

    // Геттер та сеттер для Name (яких саме і не вистачало компілятору)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}