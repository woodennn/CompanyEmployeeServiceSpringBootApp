package com.example.company_employee_service.service;

import com.example.company_employee_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Звичайний конструктор вручну замість анотації Lombok
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== [SECURITY DEBUG] Спроба входу з логіном: " + username);

        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("=== [SECURITY DEBUG] Юзера знайдено! Роль: " + user.getRole() + ", Хеш паролю: " + user.getPassword());
                    return user;
                })
                .orElseThrow(() -> {
                    System.out.println("=== [SECURITY DEBUG] Юзера '" + username + "' НЕМАЄ в базі даних!");
                    return new UsernameNotFoundException("Користувача не знайдено з логіном: " + username);
                });
    }
}