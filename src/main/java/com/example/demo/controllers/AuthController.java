package com.example.demo.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.RegisterRequest;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        System.out.println("====== LOGIN ATTEMPT ======");
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (user.getPassword().equals(password)) {
                System.out.println("✅ Login Success: " + email);
                
                // 👇 CRITICAL FIX: 500 Error thambavnyasathi Safe Map banavla
                Map<String, Object> safeUserData = new HashMap<>();
                safeUserData.put("id", user.getId());
                safeUserData.put("email", user.getEmail());
                safeUserData.put("firstName", user.getFirstName());
                safeUserData.put("lastName", user.getLastName());
                safeUserData.put("role", user.getRole() != null ? user.getRole().name() : "TENANT");
                
                // 👇 FIX: isVerified() kadhun getVerified() kela ahe
                safeUserData.put("verified", user.getVerified()); 
                safeUserData.put("token", "safe-jwt-token-123"); // Frontend la lagat hota
                
                return ResponseEntity.ok(safeUserData); 
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Wrong password!"));
            }
        } 
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found with this email!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.registerUser(request);
            
            // Register jhalyavar pan Safe Map pathvaycha
            Map<String, Object> safeUserData = new HashMap<>();
            safeUserData.put("id", newUser.getId());
            safeUserData.put("email", newUser.getEmail());
            safeUserData.put("role", newUser.getRole() != null ? newUser.getRole().name() : "TENANT");
            
            return ResponseEntity.ok(safeUserData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}