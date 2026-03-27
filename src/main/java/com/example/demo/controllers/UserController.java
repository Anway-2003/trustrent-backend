package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Property;
import com.example.demo.models.User;
import com.example.demo.repositories.PropertyRepository;
import com.example.demo.repositories.UserRepository;

@RestController
@RequestMapping("/api/users") 
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    // ==========================================
    // 1. BASIC USER APIs & ADMIN APIs
    // ==========================================

    // Sagle Users ghene (Admin Dashboard sathi) - CRASH PROOF API
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        System.out.println("====== ADMIN NE SAGLE USERS MAGITLE ======");
        
        // Fakt jevdha data lagto tevdhach Map madhe takun pathavne (Infinite Loop & Password issue solved)
        List<Map<String, Object>> safeUsers = userRepository.findAll().stream().map(user -> {
            Map<String, Object> safeUser = new HashMap<>();
            safeUser.put("id", user.getId());
            safeUser.put("firstName", user.getFirstName());
            safeUser.put("lastName", user.getLastName());
            safeUser.put("email", user.getEmail());
            safeUser.put("role", user.getRole() != null ? user.getRole().name() : "USER");
            
            // 👇 FIX: isVerified() cha getVerified() kela! 👇
            safeUser.put("verified", user.getVerified()); 
            
            safeUser.put("createdAt", user.getCreatedAt());
            return safeUser;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(safeUsers);
    }

    // Eka single user chi mahiti ghenyasti (Profile sathi)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Navin user banavne (Register sathi)
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // 👈 🟢 VIP FIX: User chi profile update karnyasti (Sagle fields add kele) 🟢
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserProfile(@PathVariable String id, @RequestBody User updatedUser) {
        System.out.println("====== PROFILE UPDATE REQUEST AALI (ID: " + id + ") ======");
        
        return userRepository.findById(id).map(existingUser -> {
            // Old fields
            if (updatedUser.getFirstName() != null) existingUser.setFirstName(updatedUser.getFirstName());
            if (updatedUser.getLastName() != null) existingUser.setLastName(updatedUser.getLastName());
            if (updatedUser.getPhone() != null) existingUser.setPhone(updatedUser.getPhone());
            if (updatedUser.getBio() != null) existingUser.setBio(updatedUser.getBio());
            if (updatedUser.getAvatar() != null) existingUser.setAvatar(updatedUser.getAvatar());
            
            // 👈 MISSING FIELDS FIX KELA: City, Region, Country
            if (updatedUser.getCity() != null) existingUser.setCity(updatedUser.getCity());
            if (updatedUser.getRegion() != null) existingUser.setRegion(updatedUser.getRegion());
            if (updatedUser.getCountry() != null) existingUser.setCountry(updatedUser.getCountry());
            
            // DB Madhe Save Maar!
            User savedUser = userRepository.save(existingUser);
            System.out.println("✅ SUCCESS: Profile saved in PostgreSQL Database!");
            
            return ResponseEntity.ok(savedUser);
        }).orElseGet(() -> {
            System.out.println("❌ ERROR: User ID sapadla nahi Database madhe!");
            return ResponseEntity.notFound().build();
        });
    }

    // ADMIN ACTION: User la Verify / Unverify karne
    @PatchMapping("/{id}/verify")
    public ResponseEntity<User> verifyUser(@PathVariable String id, @RequestBody Map<String, Boolean> updateData) {
        System.out.println("====== ADMIN VERIFICATION ACTION ======");
        return userRepository.findById(id).map(user -> {
            Boolean isVerified = updateData.get("verified");
            if (isVerified != null) {
                user.setVerified(isVerified);
                userRepository.save(user);
                System.out.println("✅ SUCCESS: User verification status changed to -> " + isVerified);
            }
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 2. FAVORITES (SAVED PROPERTIES) APIs
    // ==========================================

    // Property Save Karnyasti (Heart click)
    @PostMapping("/{userId}/favorites/{propertyId}")
    @Transactional 
    public ResponseEntity<Void> addFavorite(@PathVariable String userId, @PathVariable String propertyId) {
        return userRepository.findById(userId).map(user -> {
            return propertyRepository.findById(propertyId).map(property -> {
                user.getSavedProperties().add(property);
                userRepository.save(user);
                return ResponseEntity.ok().<Void>build();
            }).orElse(ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    // Property Remove Karnyasti (Heart unclick)
    @DeleteMapping("/{userId}/favorites/{propertyId}")
    @Transactional 
    public ResponseEntity<Void> removeFavorite(@PathVariable String userId, @PathVariable String propertyId) {
        return userRepository.findById(userId).map(user -> {
            return propertyRepository.findById(propertyId).map(property -> {
                user.getSavedProperties().remove(property);
                userRepository.save(user);
                return ResponseEntity.ok().<Void>build();
            }).orElse(ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    // Eka User chya saglya saved properties che FAKT IDs pathvne 
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<String>> getFavorites(@PathVariable String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<String> savedIds = user.getSavedProperties().stream()
                            .map(Property::getId)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(savedIds);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eka User chya saglya saved properties che FULL DETAILS pathvne (Saved Page sathi)
    @GetMapping("/{id}/favorites-details")
    public ResponseEntity<List<Property>> getFavoritePropertyDetails(@PathVariable String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        
        List<String> propertyIds = user.getSavedProperties().stream()
                                        .map(Property::getId)
                                        .toList();

        List<Property> favoriteProperties = propertyRepository.findAllById(propertyIds);
        return ResponseEntity.ok(favoriteProperties);
    }
}