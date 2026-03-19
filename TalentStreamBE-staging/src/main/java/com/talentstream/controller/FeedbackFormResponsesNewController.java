package com.talentstream.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.SaveFeedbackAnswerDTO;
import com.talentstream.service.FeedbackFormResponsesNewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class FeedbackFormResponsesNewController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackFormResponsesNewController.class);

	private final FeedbackFormResponsesNewService feedbackFormResponsesNewService;

	public FeedbackFormResponsesNewController(FeedbackFormResponsesNewService feedbackFormResponsesNewService) {
		this.feedbackFormResponsesNewService = feedbackFormResponsesNewService;
	}

	@PostMapping("/feedbackform/{feedbackFormId}/saveApplicantResponse/{applicantId}")
	public ResponseEntity<?> saveFeedback(@PathVariable Long applicantId, @PathVariable Long feedbackFormId,
			@RequestBody @Valid List<SaveFeedbackAnswerDTO> answers, BindingResult bindingResult) {

		logger.info("controller: Received request to save feedback | applicantId={} | feedbackFormId={}", applicantId,
				feedbackFormId);

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			logger.warn(
					"contoller: Validation failed while saving feedback | applicantId={} | feedbackFormId={} | errors={}",
					applicantId, feedbackFormId, errors);
			return ResponseEntity.badRequest().body(errors);
		}
		logger.info(
				"contoller: Validation passed for feedback submission | applicantId={} | feedbackFormId={} sending request to service",
				applicantId, feedbackFormId);
		String result = feedbackFormResponsesNewService.saveFeedback(applicantId, feedbackFormId, answers);
		logger.info("contoller: Feedback saved successfully | applicantId={} | feedbackFormId={}", applicantId,
				feedbackFormId);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/feedbackform/mentor/{mentorName}/calculateRatingOfMentor")
	public ResponseEntity<Object> calculateRatingOfMentor(@PathVariable String mentorName,
			@RequestParam(required = false) String collegeName,
			@RequestParam(required = false, defaultValue = "false") boolean category) {
		return ResponseEntity.ok(feedbackFormResponsesNewService.calculateRatingOfMentor(mentorName, collegeName, category));
	}

}
