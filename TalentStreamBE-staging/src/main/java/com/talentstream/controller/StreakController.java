package com.talentstream.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.StreakQuestionsDTO;
import com.talentstream.service.StreakService;

@RestController
@RequestMapping("/streak")
public class StreakController {

	private final StreakService streakService;
	private static final Logger logger = LoggerFactory.getLogger(StreakController.class);

	public StreakController(StreakService streakService) {
		this.streakService = streakService;
	}

	@PostMapping("/questions/callAI/generate")
	public ResponseEntity<?> fetchQuestionsUsingAI() {
		logger.info("Fetching questions using AI...");
		return ResponseEntity.ok(streakService.generateQuestions());
	}

	@GetMapping("/questions/{date}")
	public ResponseEntity<?> getQuestionsByDate(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		logger.info("Fetching questions for date: {}", date);
		return ResponseEntity.ok(streakService.getQuestionsByDate(date));
	}

	@PostMapping("/{applicantId}/complete")
	public ResponseEntity<String> saveStreak(@PathVariable Long applicantId) {
		logger.info("Saving today's streak for applicant ID: {}", applicantId);
		return ResponseEntity.ok(streakService.saveTodayStreak(applicantId));
	}

	@PutMapping("/{applicantId}/restore")
	public ResponseEntity<String> restoreStreak(@PathVariable Long applicantId) {
		logger.info("Restoring streak for applicant ID: {}", applicantId);
		return ResponseEntity.ok(streakService.restoreStreak(applicantId));
	}

	@GetMapping("/{applicantId}/getStreakDetails")
	public ResponseEntity<Map<String, Object>> getStreak(@PathVariable Long applicantId) {
		logger.info("Fetching streak for applicant ID: {}", applicantId);
		return ResponseEntity.ok(streakService.getStreakDetails(applicantId));
	}

	@GetMapping("{applicantId}/questions/attempted")
	public ResponseEntity<List<StreakQuestionsDTO>> getAllAttemptedQuestions(@PathVariable Long applicantId) {
		logger.info("Fetching all attempted questions for applicant ID: {}", applicantId);
		List<StreakQuestionsDTO> response = streakService.getAllAttemptedQuestions(applicantId);

		return ResponseEntity.ok(response);
	}
	@GetMapping("/{applicantId}/getAttemptedDates")
	public ResponseEntity<List<LocalDate>> getAttemptedDates(@PathVariable Long applicantId) {
 
	    logger.info("API request received for attempted dates of applicant: {}", applicantId);
 
	    List<LocalDate> dates = streakService.getAttemptedDates(applicantId);
 
	    return ResponseEntity.ok(dates);
	}
 
}
