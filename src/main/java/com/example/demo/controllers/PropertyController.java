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
@CrossOrigin(origins = "http://localhost:3000") 
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    // ==========================================
    // 🛡️ HELPER: Safe Data & Owner Verification Status
    // ==========================================
    
    // Browse page sathi short map (List dakhvnyasti)
    private Map<String, Object> makeShortSafeProperty(Property p) {
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
        
        if (p.getOwner() != null) {
            safeMap.put("ownerId", p.getOwner().getId());
            safeMap.put("ownerVerified", p.getOwner().getVerified()); 
        } else if (p.getOwnerId() != null) {
            safeMap.put("ownerId", p.getOwnerId());
            safeMap.put("ownerVerified", false);
        }
        return safeMap;
    }

    // 1. Sagle properties dakhvnyasathi (Browse Page) 
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProperties() {
        System.out.println("====== BROWSE PAGE NE SAGLE PROPERTIES MAGITLE ======");
        List<Map<String, Object>> safeProperties = propertyRepository.findAll()
                .stream()
                .filter(p -> p.getOwner() != null && Boolean.TRUE.equals(p.getOwner().getVerified()))
                .map(this::makeShortSafeProperty) // List sathi chota map
                .collect(Collectors.toList());
        return ResponseEntity.ok(safeProperties);
    }

    // 👈 🟢 VIP FIX: Single Property ghetana PURNA DATA dakhvane (Owner Details sakt!)
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable String id) {
        // Aata aapan Map nahi, direct Property object pathvat ahot, 
        // mhanje frontend la owner cha phone, firstName, avatar sagle bhetel!
        return propertyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Eka specific malakachi (Owner) sagli ghar shodhnyasathi (My Properties)
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Map<String, Object>>> getPropertiesByOwner(@PathVariable String ownerId) {
        List<Map<String, Object>> safeProperties = propertyRepository.findByOwnerId(ownerId)
                .stream().map(this::makeShortSafeProperty).collect(Collectors.toList());
        return ResponseEntity.ok(safeProperties);
    }

    // 4. Navin property save karnyasti
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProperty(@RequestBody Property property) {
        if (property.getOwnerId() != null) {
            User dummyOwner = new User();
            dummyOwner.setId(property.getOwnerId());
            property.setOwner(dummyOwner); 
        }
        property.setAvailable(true); 
        Property savedProperty = propertyRepository.save(property);
        return ResponseEntity.ok(makeShortSafeProperty(savedProperty));
    }

    // 5. Property Update karnyasti
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProperty(@PathVariable String id, @RequestBody Property updatedProperty) {
        return propertyRepository.findById(id)
                .map(existingProperty -> {
                    existingProperty.setAvailable(updatedProperty.isAvailable());
                    if (updatedProperty.getTitle() != null) existingProperty.setTitle(updatedProperty.getTitle());
                    if (updatedProperty.getCity() != null) existingProperty.setCity(updatedProperty.getCity());
                    if (updatedProperty.getRegion() != null) existingProperty.setRegion(updatedProperty.getRegion());
                    if (updatedProperty.getMonthlyRent() > 0) existingProperty.setMonthlyRent(updatedProperty.getMonthlyRent());
                    if (updatedProperty.getRooms() > 0) existingProperty.setRooms(updatedProperty.getRooms());

                    Property savedProperty = propertyRepository.save(existingProperty);
                    return ResponseEntity.ok(makeShortSafeProperty(savedProperty));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. Delete property
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {
        if (propertyRepository.existsById(id)) {
            propertyRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}