package com.talentstream.repository;

import com.talentstream.entity.FeedbackAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackAnswer, Long> { }
