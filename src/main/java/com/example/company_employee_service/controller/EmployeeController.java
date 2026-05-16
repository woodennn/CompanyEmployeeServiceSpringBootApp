package com.example.company_employee_service.controller;

import com.example.company_employee_service.model.User;
import com.example.company_employee_service.repository.UserRepository;
import com.example.company_employee_service.model.Employee;
import com.example.company_employee_service.model.Department;
import com.example.company_employee_service.repository.EmployeeRepository;
import com.example.company_employee_service.repository.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.security.Principal;

@Controller
@RequestMapping("/")
public class EmployeeController {
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository deptRepo;
    private final UserRepository userRepo;

    public EmployeeController(EmployeeRepository employeeRepo,
                              DepartmentRepository deptRepo,
                              UserRepository userRepo) {
        this.employeeRepo = employeeRepo;
        this.deptRepo = deptRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String index(Model model, Principal principal) {
        if (principal != null) {
            boolean isCustomer = SecurityContextHolder.getContext()
                    .getAuthentication().getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

            if (isCustomer) {
                return "redirect:/cabinet";
            }
        }
        model.addAttribute("employees", employeeRepo.findAll());
        model.addAttribute("departments", deptRepo.findAll());
        return "index";
    }

    @PostMapping("/department/add")
    public String addDept(@RequestParam String name) {
        Department dept = new Department();
        dept.setName(name);
        deptRepo.save(dept);
        return "redirect:/";
    }

    @GetMapping("/employee/new-form")
    public String employeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", deptRepo.findAll());
        return "add-employee";
    }

    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute Employee employee,
                               @RequestParam(required = false) Long departmentId,
                               @RequestParam String username) {

        if (departmentId != null) {
            Department dept = deptRepo.findById(departmentId).orElse(null);
            employee.setDepartment(dept);
        }

        if (username != null && !username.trim().isEmpty()) {
            String cleanUsername = username.trim();

            // Захист від DataIntegrityViolationException: спочатку шукаємо існуючого юзера в базі H2
            User existingUser = userRepo.findAll().stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(cleanUsername))
                    .findFirst()
                    .orElse(null);

            if (existingUser != null) {
                employee.setUser(existingUser);
            } else {
                User spaceUser = new User();
                spaceUser.setUsername(cleanUsername);
                spaceUser.setRole("CUSTOMER");
                spaceUser.setPassword("");

                User savedUser = userRepo.save(spaceUser);
                employee.setUser(savedUser);
            }
        }

        employeeRepo.save(employee);
        return "redirect:/";
    }

    @GetMapping("/employee/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepo.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/cabinet")
    public String showCabinet(Model model, Principal principal) {
        String currentUsername = principal.getName();
        Employee employee = employeeRepo.findByUserUsername(currentUsername);

        if (employee == null) {
            model.addAttribute("error", "До вашого акаунту (" + currentUsername + ") ще не прив'язано картку співробітника в локальній БД.");
        } else {
            model.addAttribute("employee", employee);
        }
        return "cabinet";
    }
}