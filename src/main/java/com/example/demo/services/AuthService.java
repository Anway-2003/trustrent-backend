package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.RegisterRequest;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(RegisterRequest request) {
        // 1. Check kar email aadhich ahe ka
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bhava, ha Email aadhich exist ahe!");
        }

        // 2. Navin User object banav ani data set kar
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Aata sadhyasathi plain text, nantar security lau
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // 👇 MAIN FIX: Enum la String madhe convert kela (.name() vaprun)
        if (request.getRole() != null) {
            user.setRole(request.getRole().name());
        }

        // 3. Database madhe save kar
        return userRepository.save(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}