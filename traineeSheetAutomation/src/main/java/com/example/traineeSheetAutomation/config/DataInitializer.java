package com.example.traineeSheetAutomation.config;

import com.example.traineeSheetAutomation.entity.Role;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.RoleName;
import com.example.traineeSheetAutomation.repository.RoleRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if not exist
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = new Role();
                role.setRoleName(roleName);
                roleRepository.save(role);
                System.out.println("Created role: " + roleName);
            }
        }

        // Create admin user if not exist
        if (!userRepository.existsByEmail("admin@example.com")) {
            Role managerRole = roleRepository.findByRoleName(RoleName.MANAGER)
                    .orElseThrow(() -> new RuntimeException("Manager role not found"));

            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(managerRole);

            userRepository.save(admin);
            System.out.println("Created admin user");
        }
    }
}