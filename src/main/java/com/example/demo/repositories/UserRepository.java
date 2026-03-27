package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Email varun user shodhnyasathi (Login sathi)
    Optional<User> findByEmail(String email);

    // Email aadhich database madhe ahe ka te check karnyasti (Signup sathi) -> HI NAVIN LINE AHE
    boolean existsByEmail(String email); 

    
}