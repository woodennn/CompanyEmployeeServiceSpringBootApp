package com.example.company_employee_service.controller;

import com.example.company_employee_service.model.Employee;
import com.example.company_employee_service.model.Department;
import com.example.company_employee_service.repository.EmployeeRepository;
import com.example.company_employee_service.repository.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class EmployeeController {
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository deptRepo;

    public EmployeeController(EmployeeRepository employeeRepo, DepartmentRepository deptRepo) {
        this.employeeRepo = employeeRepo;
        this.deptRepo = deptRepo;
    }

    // Головна сторінка зі списком
    @GetMapping
    public String index(Model model) {
        model.addAttribute("employees", employeeRepo.findAll());
        model.addAttribute("departments", deptRepo.findAll());
        return "index";
    }

    // Збереження нового департаменту
    @PostMapping("/department/add")
    public String addDept(@RequestParam String name) {
        Department dept = new Department();
        dept.setName(name);
        deptRepo.save(dept);
        return "redirect:/";
    }

    // Форма додавання співробітника
    @GetMapping("/employee/add")
    public String employeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", deptRepo.findAll());
        return "add-employee";
    }

    // Збереження співробітника
    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute Employee employee) {
        employeeRepo.save(employee);
        return "redirect:/";
    }

    // Видалення співробітника
    @GetMapping("/employee/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepo.deleteById(id);
        return "redirect:/";
    }
}