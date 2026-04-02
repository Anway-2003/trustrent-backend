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
@CrossOrigin(origins = {"http://localhost:3000", "https://trustrent-frontend.vercel.app"})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    // 👈 🟢 UPDATED: GOOGLE LOGIN WITH isNewUser FLAG 🟢
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String name = data.get("name");
        String image = data.get("image");

        System.out.println("====== GOOGLE LOGIN ATTEMPT: " + email + " ======");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        boolean isNewUser = false; // 👈 HA FLAG KHUP IMP AHE

        if (userOptional.isPresent()) {
            user = userOptional.get();
            System.out.println("✅ Existing Google User Found");
        } else {
            // JAR USER NASEL TAR AUTO-REGISTER KARA
            System.out.println("🆕 New Google User - Creating Account");
            isNewUser = true; // Flag true kara
            user = new User();
            user.setEmail(email);
            
            // Dummy password for DB constraints
            user.setPassword("GOOGLE_AUTH_" + Math.random());
            
            // Name split logic
            if (name != null && name.contains(" ")) {
                user.setFirstName(name.split(" ")[0]);
                user.setLastName(name.substring(name.indexOf(" ") + 1));
            } else {
                user.setFirstName(name != null ? name : "User");
                user.setLastName("");
            }
            
            user.setAvatar(image);
            user.setVerified(true); 
            user.setRole(null); // Role survatila null theva, user select karel
            
            userRepository.save(user);
        }

        // Safe map madhe isNewUser flag add kara
        Map<String, Object> safeData = convertToSafeMap(user);
        safeData.put("isNewUser", isNewUser); 
        
        return ResponseEntity.ok(safeData);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        System.out.println("====== NORMAL LOGIN ATTEMPT ======");
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                return ResponseEntity.ok(convertToSafeMap(user)); 
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials!"));
            }
        } 
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.registerUser(request);
            return ResponseEntity.ok(convertToSafeMap(newUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 👈 🟢 VIP FIX: नवीन API जो Role डेटाबेसमध्ये सेव्ह करेल (String वापरून फिक्स केला) 🟢
    @PostMapping("/update-role")
    public ResponseEntity<?> updateRole(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String roleStr = request.get("role");

        System.out.println("====== UPDATING ROLE FOR: " + email + " TO: " + roleStr + " ======");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 👈 FIX: आता आपण डायरेक्ट String व्हॅल्यू पास करत आहोत
            if ("LANDLORD".equalsIgnoreCase(roleStr)) {
                user.setRole("LANDLORD");
            } else if ("TENANT".equalsIgnoreCase(roleStr)) {
                user.setRole("TENANT");
            }

            // डेटाबेसमध्ये सेव्ह करा
            userRepository.save(user);
            System.out.println("✅ Role updated successfully!");

            // अपडेटेड युजर डेटा Frontend ला परत पाठवा
            return ResponseEntity.ok(convertToSafeMap(user));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "User not found"));
    }

    // 🛠️ HELPER METHOD: Frontend la safe data pathvnyasathi
    private Map<String, Object> convertToSafeMap(User user) {
        Map<String, Object> safeData = new HashMap<>();
        safeData.put("id", user.getId());
        safeData.put("email", user.getEmail());
        safeData.put("firstName", user.getFirstName());
        safeData.put("lastName", user.getLastName());
        
        // Role check
        safeData.put("role", user.getRole() != null ? user.getRole().toString() : null);
        
        // 👈 FIX: Boolean unboxing error fix केला.
        safeData.put("verified", Boolean.TRUE.equals(user.getVerified())); 
        
        safeData.put("token", "safe-jwt-token-123"); 
        safeData.put("phone", user.getPhone());
        safeData.put("avatar", user.getAvatar());
        return safeData;
    }
}