package com.example.traineeSheetAutomation.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Hashes a plain text password
    public String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

}
