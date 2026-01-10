package com.talentstream.repository;

import com.talentstream.entity.FeedbackResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, Long> {

    @EntityGraph(attributePaths = "answers") // fetch answers with the response
    List<FeedbackResponse> findByFormIdOrderByCreatedAtDesc(Long formId);
}
