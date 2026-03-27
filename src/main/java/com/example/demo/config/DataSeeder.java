package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        
        String adminEmail = "admin@trustrent.com";

        // Check karne ki admin aadhi pasun ahe ka
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            System.out.println("====== VIP ADMIN ENTRY HOTIYE ======");
            
            User admin = new User();
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword("admin123"); 
            
            // 👇 Hya line var error hota, aata to fix zala!
            admin.setRole("ADMIN"); 
            
            admin.setVerified(true); 
            
            userRepository.save(admin);
            System.out.println("✅ Default Admin Tayar Zala!");
            System.out.println("👉 ID: " + adminEmail);
            System.out.println("👉 Pass: admin123");
        } else {
            System.out.println("👍 Admin aadhi pasunch database madhe ahe.");
        }
    }
}