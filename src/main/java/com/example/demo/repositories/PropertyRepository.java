package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
    List<Property> findByCityIgnoreCase(String city);
    List<Property> findByOwnerId(String ownerId);
    List<Property> findByAvailableTrue();
}