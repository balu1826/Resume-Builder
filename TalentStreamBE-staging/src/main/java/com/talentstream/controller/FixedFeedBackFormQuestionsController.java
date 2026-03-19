package com.talentstream.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.CreateNewFeedBackFormQuestionDTO;
import com.talentstream.service.FixedFeedBackFormQuestionsService;

@RestController
@RequestMapping("/recruiter/{recruiterId}/fixed-feedback-questions")
public class FixedFeedBackFormQuestionsController {

	private static final Logger logger = LoggerFactory.getLogger(FixedFeedBackFormQuestionsController.class);

	private final FixedFeedBackFormQuestionsService service;

	public FixedFeedBackFormQuestionsController(FixedFeedBackFormQuestionsService service) {
		this.service = service;
	}

	@PostMapping("/create")
	public ResponseEntity<?> createFixedFeedbackQuestion(@Valid @RequestBody CreateNewFeedBackFormQuestionDTO question,
			BindingResult bindingResult) {

		logger.info("Received request to create fixed feedback question");

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

			logger.warn("Validation failed while creating fixed feedback question | errors={}", errors);

			return ResponseEntity.badRequest().body(errors);
		}

		service.createFeedbackFormQuestion(question);

		logger.info("Fixed feedback question created successfully");

		return ResponseEntity.ok("Fixed feedback question created successfully");
	}
}
