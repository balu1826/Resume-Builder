package com.talentstream.repository;

import com.talentstream.entity.FeedbackForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FeedbackFormRepository extends JpaRepository<FeedbackForm, Long> {
    // Spring Data JPA automatically implements this because 'createdAt' exists
    Optional<FeedbackForm> findTopByOrderByCreatedAtDesc();
}
