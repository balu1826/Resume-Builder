package com.talentstream.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.talentstream.dto.StreakQuestionsDTO;
import com.talentstream.entity.StreakQuestions;

public interface StreakQuestionsRepository extends JpaRepository<StreakQuestions, Long> {

	List<StreakQuestions> findByQuestionIn(List<String> generatedQuestions);

	@Query("SELECT s.postDate, COUNT(s) FROM StreakQuestions s GROUP BY s.postDate")
	List<Object[]> countQuestionsGroupedByDate();

	@Query("SELECT MAX(s.postDate) FROM StreakQuestions s")
	LocalDate findMaxPostDate();

	@Query("SELECT new com.talentstream.dto.StreakQuestionsDTO(" + "s.question, " + "s.description, "
			+ "s.correctAnswer, " + "s.optionsJson) " + "FROM StreakQuestions s " + "WHERE s.postDate = :postDate "
			+ "ORDER BY s.id ASC")
	List<StreakQuestionsDTO> findByPostDateOrderByIdAsc(LocalDate postDate);

	long countByPostDate(LocalDate lastDate);

	List<StreakQuestions> findByPostDateInOrderByPostDateDesc(List<LocalDate> attemptedDates);
}