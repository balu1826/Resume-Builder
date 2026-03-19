package com.talentstream.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.CreateFeedbackFormsDto;
import com.talentstream.dto.FeedbackFormsResponseDTO;
import com.talentstream.dto.GetFeedbackFormByIdDTO;
import com.talentstream.service.FeedbackFormsNewService;

@RestController
@RequestMapping("api/feedbackforms")
public class FeedbackFormsNewController {

	private final FeedbackFormsNewService feedbackFormsNewService;
	private static final Logger logger = LoggerFactory.getLogger(FeedbackFormsNewController.class);

	public FeedbackFormsNewController(FeedbackFormsNewService feedbackFormsNewService) {
		this.feedbackFormsNewService = feedbackFormsNewService;
	}

	@PostMapping("/recruiter/{recruiterId}/createFeedbackForm")
	public ResponseEntity<?> createFeedbackForm(@PathVariable Long recruiterId,
			@Valid @RequestBody CreateFeedbackFormsDto createfeedbackFormDto, BindingResult bindingResult) {

		logger.info("Received request to create feedback form for recruiter ID: {}", recruiterId);

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			logger.warn("Validation failed for feedback form creation: {}", errors);
			return ResponseEntity.badRequest().body(errors);
		}

		String response = feedbackFormsNewService.createFeedbackForm(recruiterId, createfeedbackFormDto);
		logger.info("Successfully created feedback form for recruiter ID: {}", recruiterId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/getfeedbackFormById/{formId}")
	public ResponseEntity<?> getFeedbackFormById(@PathVariable Long formId) {
		logger.info("Received request to get feedback form by ID: {}", formId);
		GetFeedbackFormByIdDTO feedbackFormDto = feedbackFormsNewService.getFeedbackFormById(formId);
		logger.info("Successfully retrieved feedback form for ID: {}", formId);
		return ResponseEntity.ok(feedbackFormDto);
	}

	@GetMapping("/getallfeedbackforms")
	public ResponseEntity<?> getAllActiveFeedbackForms() {
		logger.info("Received request to get all feedback forms");
		List<FeedbackFormsResponseDTO> feedbackForms = feedbackFormsNewService.getAllActiveFeedbackForms();
		logger.info("Successfully retrieved all feedback forms, count: {}", feedbackForms.size());
		return ResponseEntity.ok(feedbackForms);
	}

	@PutMapping("/recruiter/updateFeedBackFormById/{formId}")
	public ResponseEntity<?> updateFeedbackForm( @PathVariable Long formId,
			@Valid @RequestBody CreateFeedbackFormsDto createfeedbackFormDto, BindingResult bindingResult) {

		logger.info("Received request to create feedback form for form ID: {}", formId);

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			logger.warn("Validation failed for feedback form creation: {}", errors);
			return ResponseEntity.badRequest().body(errors);
		}
		String response = feedbackFormsNewService.updateFeedbackForm(formId, createfeedbackFormDto);
		logger.info("Successfully updated feedback form ID: {}", formId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/recruiter/getallfeedbackforms")
	public ResponseEntity<?> getAllFeedbackForms(@RequestParam(required = false) Long recruiterId) {
		logger.info("Received request to get all feedback forms for recruiter ID: {}", recruiterId);
		List<FeedbackFormsResponseDTO> feedbackForms = feedbackFormsNewService.getAllFeedbackForms(recruiterId);
		logger.info("Successfully retrieved all feedback forms for recruiter ID: {}", recruiterId);
		return ResponseEntity.ok(feedbackForms);
	}

	@DeleteMapping("/recruiter/deleteFeedbackFormById/{formId}")
	public ResponseEntity<?> deleteFeedbackFormById(@PathVariable Long formId) {
		logger.info("Received request to delete feedback form by ID: {}", formId);
		String response = feedbackFormsNewService.deleteFeedbackFormById(formId);
		logger.info("Successfully deleted feedback form for ID: {}", formId);
		return ResponseEntity.ok(response);
	}

	//get all mentor names and collge names of the mentor
	@GetMapping("/getAllMentorNamesAndCollegeNames")
	public ResponseEntity<?> getAllMentorNamesAndCollegeNames() {
		logger.info("Received request to get all mentor names and college names");
		Map<String, Set<String>> mentorCollegeMap = feedbackFormsNewService.getAllMentorNamesAndCollegeNames();
		logger.info("Successfully retrieved mentor names and college names");
		return ResponseEntity.ok(mentorCollegeMap);
	}

}
