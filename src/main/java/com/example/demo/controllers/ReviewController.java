package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.enums.ReviewType;
import com.example.demo.models.Review;
import com.example.demo.models.User;
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.repositories.UserRepository;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    // ==========================================
    // 1. Submit Feedback (By Default isApproved = False)
    // ==========================================
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Map<String, String> data) {
        try {
            String giverId = data.get("giverId");
            String receiverId = data.get("receiverId"); // Admin kiva platform cha ID asu shakto
            
            Optional<User> giverOpt = userRepository.findById(giverId);
            Optional<User> receiverOpt = userRepository.findById(receiverId);

            if (giverOpt.isPresent() && receiverOpt.isPresent()) {
                Review review = new Review();
                review.setGiver(giverOpt.get());
                review.setReceiver(receiverOpt.get());
                review.setRating(Integer.parseInt(data.get("rating")));
                review.setComment(data.get("comment"));
                review.setType(ReviewType.valueOf(data.getOrDefault("type", "PLATFORM_REVIEW")));
                review.setIsApproved(false); // 👈 Admin Verify karel!

                reviewRepository.save(review);
                return ResponseEntity.ok(Map.of("message", "Feedback submitted! Waiting for Admin approval."));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 2. Get APPROVED Reviews (Home Page Sathi)
    // ==========================================
    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedReviews() {
        List<Map<String, Object>> safeReviews = reviewRepository.findByIsApprovedTrueOrderByCreatedAtDesc()
            .stream().map(this::convertToSafeMap).collect(Collectors.toList());
        return ResponseEntity.ok(safeReviews);
    }

    // ==========================================
    // 3. Get ALL Reviews (Admin Dashboard Sathi)
    // ==========================================
    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews() {
        List<Map<String, Object>> safeReviews = reviewRepository.findAllByOrderByCreatedAtDesc()
            .stream().map(this::convertToSafeMap).collect(Collectors.toList());
        return ResponseEntity.ok(safeReviews);
    }

    // ==========================================
    // 4. Approve / Reject Feedback (Admin Action)
    // ==========================================
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> toggleApproval(@PathVariable String id, @RequestBody Map<String, Boolean> data) {
        return reviewRepository.findById(id).map(review -> {
            review.setIsApproved(data.get("isApproved"));
            reviewRepository.save(review);
            return ResponseEntity.ok(Map.of("message", "Feedback status updated!"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🛠️ Helper Method: Frontend la crash pasun vachavnyasti safe data pathvne
    private Map<String, Object> convertToSafeMap(Review review) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", review.getId());
        map.put("rating", review.getRating());
        map.put("comment", review.getComment());
        map.put("type", review.getType());
        map.put("isApproved", review.getIsApproved());
        map.put("createdAt", review.getCreatedAt());

        if (review.getGiver() != null) {
            map.put("giverName", review.getGiver().getFirstName() + " " + review.getGiver().getLastName());
            map.put("giverAvatar", review.getGiver().getAvatar());
            map.put("giverRole", review.getGiver().getRole());
        }
        return map;
    }
}