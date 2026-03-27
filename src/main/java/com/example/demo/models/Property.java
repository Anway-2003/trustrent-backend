package com.example.demo.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <-- Hi line badalli ahe

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private String ownerId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String type;
    private String address;
    private String city;
    private String region;
    private String country;

    private Integer rooms;
    private Integer bathrooms;
    private Double area;
    private String floor;

    private boolean hasElevator;
    private boolean hasParking;
    private boolean hasBalcony;
    private boolean hasGarden;
    private boolean furnished;
    private boolean petsAllowed;
    private boolean smokingAllowed;

    private Double monthlyRent;
    private Double deposit;
    private Double utilities;

    private String availableFrom;
    private String rentalPeriod;
    private Integer minRentalMonths;
    private Integer maxRentalMonths;

    private boolean available = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "TEXT") 
    private List<String> images;

    // 👇 HA AHE MASTERSTROKE (Error Fix) 👇
    // Apan @JsonIgnore kadhun @JsonIgnoreProperties vaparla ahe, mhanje to frontend kadun Data gheil pan Infinite loop honar nahi!
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"properties", "hibernateLazyInitializer", "handler"}) 
    private User owner;

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getRooms() { return rooms; }
    public void setRooms(Integer rooms) { this.rooms = rooms; }

    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public boolean isHasElevator() { return hasElevator; }
    public void setHasElevator(boolean hasElevator) { this.hasElevator = hasElevator; }

    public boolean isHasParking() { return hasParking; }
    public void setHasParking(boolean hasParking) { this.hasParking = hasParking; }

    public boolean isHasBalcony() { return hasBalcony; }
    public void setHasBalcony(boolean hasBalcony) { this.hasBalcony = hasBalcony; }

    public boolean isHasGarden() { return hasGarden; }
    public void setHasGarden(boolean hasGarden) { this.hasGarden = hasGarden; }

    public boolean isFurnished() { return furnished; }
    public void setFurnished(boolean furnished) { this.furnished = furnished; }

    public boolean isPetsAllowed() { return petsAllowed; }
    public void setPetsAllowed(boolean petsAllowed) { this.petsAllowed = petsAllowed; }

    public boolean isSmokingAllowed() { return smokingAllowed; }
    public void setSmokingAllowed(boolean smokingAllowed) { this.smokingAllowed = smokingAllowed; }

    public Double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(Double monthlyRent) { this.monthlyRent = monthlyRent; }

    public Double getDeposit() { return deposit; }
    public void setDeposit(Double deposit) { this.deposit = deposit; }

    public Double getUtilities() { return utilities; }
    public void setUtilities(Double utilities) { this.utilities = utilities; }

    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }

    public String getRentalPeriod() { return rentalPeriod; }
    public void setRentalPeriod(String rentalPeriod) { this.rentalPeriod = rentalPeriod; }

    public Integer getMinRentalMonths() { return minRentalMonths; }
    public void setMinRentalMonths(Integer minRentalMonths) { this.minRentalMonths = minRentalMonths; }

    public Integer getMaxRentalMonths() { return maxRentalMonths; }
    public void setMaxRentalMonths(Integer maxRentalMonths) { this.maxRentalMonths = maxRentalMonths; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

// ==========================================
    // EQUALS & HASHCODE (Favorites fix karnyasti)
    // ==========================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        Property property = (Property) o;
        return id != null && id.equals(property.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}