package com.example.company_employee_service.repository;

import com.example.company_employee_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Вказуємо Spring Data JPA чіткий запит: шукати Employee, у якого в об'єкті user поле username збігається
    @Query("SELECT e FROM Employee e WHERE e.user.username = :username")
    Employee findByUserUsername(@Param("username") String username);
}