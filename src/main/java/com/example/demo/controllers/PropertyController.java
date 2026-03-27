package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Property;
import com.example.demo.models.User;
import com.example.demo.repositories.PropertyRepository;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000") // Frontend la allow karnyasti
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    // ==========================================
    // 🛡️ HELPER: Infinite Loop (Crash) thambavnyasathi
    // ==========================================
    private Map<String, Object> makeSafeProperty(Property p) {
        Map<String, Object> safeMap = new HashMap<>();
        safeMap.put("id", p.getId());
        safeMap.put("title", p.getTitle());
        safeMap.put("type", p.getType());
        safeMap.put("city", p.getCity());
        safeMap.put("region", p.getRegion());
        safeMap.put("monthlyRent", p.getMonthlyRent());
        safeMap.put("available", p.isAvailable());
        safeMap.put("images", p.getImages());
        safeMap.put("rooms", p.getRooms());
        
        // Owner ID safe paddhatine takne (User object avoid karun)
        if (p.getOwnerId() != null) {
            safeMap.put("ownerId", p.getOwnerId());
        } else if (p.getOwner() != null) {
            safeMap.put("ownerId", p.getOwner().getId());
        }
        return safeMap;
    }

    // 1. Sagle properties dakhvnyasathi (Browse Page) - CRASH PROOF ✅
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProperties() {
        System.out.println("====== BROWSE PAGE NE SAGLE PROPERTIES MAGITLE ======");
        List<Map<String, Object>> safeProperties = propertyRepository.findAll()
                .stream().map(this::makeSafeProperty).collect(Collectors.toList());
        return ResponseEntity.ok(safeProperties);
    }

    // 2. ID varun ek property shodhnyasathi (View Details sathi) - CRASH PROOF ✅
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPropertyById(@PathVariable String id) {
        return propertyRepository.findById(id)
                .map(p -> ResponseEntity.ok(makeSafeProperty(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Eka specific malakachi (Owner) sagli ghar shodhnyasathi (My Properties) - CRASH PROOF ✅
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Map<String, Object>>> getPropertiesByOwner(@PathVariable String ownerId) {
        List<Map<String, Object>> safeProperties = propertyRepository.findByOwnerId(ownerId)
                .stream().map(this::makeSafeProperty).collect(Collectors.toList());
        return ResponseEntity.ok(safeProperties);
    }

    // 4. Navin property save karnyasti (Add Property)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProperty(@RequestBody Property property) {
        System.out.println("====== NAVIN PROPERTY ADD HOTIYE ======");
        
        if (property.getOwnerId() != null) {
            User dummyOwner = new User();
            dummyOwner.setId(property.getOwnerId());
            property.setOwner(dummyOwner); 
            System.out.println("✅ SUCCESS: Owner attach jhala!");
        } else {
            System.out.println("🚨 DANGER: Owner ID aala nahi!");
        }

        property.setAvailable(true); // Default true
        Property savedProperty = propertyRepository.save(property);
        return ResponseEntity.ok(makeSafeProperty(savedProperty));
    }

    // 5. Property Update (Edit / Mark as Rented) karnyasti
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProperty(@PathVariable String id, @RequestBody Property updatedProperty) {
        System.out.println("====== UPDATE BUTTON DABLAAA ======");
        
        return propertyRepository.findById(id)
                .map(existingProperty -> {
                    existingProperty.setAvailable(updatedProperty.isAvailable());
                    
                    if (updatedProperty.getTitle() != null) existingProperty.setTitle(updatedProperty.getTitle());
                    if (updatedProperty.getCity() != null) existingProperty.setCity(updatedProperty.getCity());
                    if (updatedProperty.getRegion() != null) existingProperty.setRegion(updatedProperty.getRegion());
                    if (updatedProperty.getMonthlyRent() > 0) existingProperty.setMonthlyRent(updatedProperty.getMonthlyRent());
                    if (updatedProperty.getRooms() > 0) existingProperty.setRooms(updatedProperty.getRooms());

                    Property savedProperty = propertyRepository.save(existingProperty);
                    System.out.println("✅ SUCCESS: Status Update Jhala!");
                    return ResponseEntity.ok(makeSafeProperty(savedProperty));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. Property Delete karnyasti
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {
        if (propertyRepository.existsById(id)) {
            propertyRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}