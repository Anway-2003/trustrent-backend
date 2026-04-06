package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.example.demo.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(exclude = "savedProperties") // Lombok cha infinite loop thambavnyasathi
@ToString(exclude = "savedProperties") // Lombok cha infinite loop thambavnyasathi
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "phone")
    private String phone;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar; 
    
    private String city;
    private String region;
    private String country;

    // 👇 नवीन: डॉक्युमेंट (Aadhaar/PAN) ची लिंक सेव्ह करण्यासाठी 👇
    @Column(name = "gov_id_url", columnDefinition = "TEXT")
    private String govIdUrl;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // Saved Properties (Favorites) sathi
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_saved_properties",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "property_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Property> savedProperties = new HashSet<>();

    // MAIN FIX: 'boolean' ch 'Boolean' kela ahe. Aata NullPointerException yenar nahi!
    @Column(name = "verified")
    private Boolean verified = false; 

    // String la Enum madhe convert karnyachi custom method
    public void setRole(String roleName) {
        if (roleName != null) {
            this.role = UserRole.valueOf(roleName.toUpperCase());
        }
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    // 👇 Lombok चा एरर येऊ नये म्हणून Gov ID च्या Custom Methods 👇
    public String getGovIdUrl() { return govIdUrl; }
    public void setGovIdUrl(String govIdUrl) { this.govIdUrl = govIdUrl; }
}