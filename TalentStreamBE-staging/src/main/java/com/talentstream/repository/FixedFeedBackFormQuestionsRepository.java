package com.talentstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.FixedFeedBackFormQuestions;

@Repository
public interface FixedFeedBackFormQuestionsRepository
        extends JpaRepository<FixedFeedBackFormQuestions, Long> {

    List<FixedFeedBackFormQuestions> findAllByOrderByIdAsc();
    @Query(
    	    "select q.questionKey " +
    	    "from FixedFeedBackFormQuestions q " +
    	    "where q.isRequired = true"
    	)
    	List<String> findRequiredQuestionKeys();

    
}

