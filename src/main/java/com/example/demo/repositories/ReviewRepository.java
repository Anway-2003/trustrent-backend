package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.models.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    
    // फक्त 'Approved' झालेले फीडबॅक काढण्यासाठी (Home Page साठी)
    List<Review> findByIsApprovedTrueOrderByCreatedAtDesc();
    
    // सर्व फीडबॅक काढण्यासाठी (Admin Dashboard साठी)
    List<Review> findAllByOrderByCreatedAtDesc();
}