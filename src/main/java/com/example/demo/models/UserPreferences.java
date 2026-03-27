package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.enums.PropertyType;
import com.example.demo.enums.RentalPeriod;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_preferences")
@Data
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Double minBudget;
    private Double maxBudget;

    @ElementCollection
    @CollectionTable(name = "user_preferred_cities", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "city")
    private List<String> preferredCities;

    private Integer maxDistanceKm;

    @ElementCollection
    @CollectionTable(name = "user_preferred_property_types", joinColumns = @JoinColumn(name = "preference_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type")
    private List<PropertyType> preferredPropertyTypes;

    private Integer minRooms;
    private Integer maxRooms;
    private Boolean petsAllowed;
    private Boolean smokingAllowed;

    @Enumerated(EnumType.STRING)
    private RentalPeriod preferredRentalPeriod;

    private Integer minRentalMonths;
    private Integer maxRentalMonths;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}