package com.example.company_employee_service.controller;

import com.example.company_employee_service.model.User;
import com.example.company_employee_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.company_employee_service.model.Employee;
import com.example.company_employee_service.model.Department;
import com.example.company_employee_service.repository.EmployeeRepository;
import com.example.company_employee_service.repository.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/")
public class EmployeeController {
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository deptRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public EmployeeController(EmployeeRepository employeeRepo,
                              DepartmentRepository deptRepo,
                              UserRepository userRepo,
                              PasswordEncoder passwordEncoder) {
        this.employeeRepo = employeeRepo;
        this.deptRepo = deptRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
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
    public String saveEmployee(@ModelAttribute Employee employee,
                               @RequestParam Long departmentId,
                               @RequestParam String username,
                               @RequestParam String password) {

        if (departmentId != null) {
            Department dept = deptRepo.findById(departmentId).orElse(null);
            employee.setDepartment(dept);
        }

        if (!username.isEmpty() && !password.isEmpty()) {
            User spaceUser = new User();
            spaceUser.setUsername(username);
            spaceUser.setPassword(passwordEncoder.encode(password));
            spaceUser.setRole("CUSTOMER");

            userRepo.save(spaceUser);
            employee.setUser(spaceUser);
        }

        employeeRepo.save(employee);
        return "redirect:/";
    }

    // Видалення співробітника
    @GetMapping("/employee/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepo.deleteById(id);
        return "redirect:/";
    }
    @GetMapping("/cabinet")
    public String showCabinet(Model model, Principal principal) {
        // Отримуємо логін користувача, який зараз увійшов у систему
        String currentUsername = principal.getName();

        // Шукаємо працівника, прив'язаного саме до цього користувача (розмежування даних)
        Employee employee = employeeRepo.findByUserUsername(currentUsername);

        if (employee == null) {
            model.addAttribute("error", "До вашого акаунту ще не прив'язано картку співробітника.");
        } else {
            model.addAttribute("employee", employee);
        }

        return "cabinet"; // назва HTML-файлу
    }
}