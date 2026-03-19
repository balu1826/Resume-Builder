package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talentstream.dto.SaveFeedbackAnswerDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.FeedbackFormResponsesNew;
import com.talentstream.entity.FeedbackFormsNew;
import com.talentstream.entity.RatingCategory;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.FeedbackFormResponsesNewRepository;
import com.talentstream.repository.FeedbackFormsNewRepository;
import com.talentstream.repository.FixedFeedBackFormQuestionsRepository;

@Service
public class FeedbackFormResponsesNewService {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackFormResponsesNewService.class);

	private final FeedbackFormResponsesNewRepository answersRepo;
	private final FixedFeedBackFormQuestionsRepository questionRepository;
	private final FeedbackFormsNewRepository formsRepo;
	private final ApplicantRepository applicantRepo;

	public FeedbackFormResponsesNewService(FeedbackFormResponsesNewRepository answersRepo,
			FixedFeedBackFormQuestionsRepository questionRepository, FeedbackFormsNewRepository formsRepo,
			ApplicantRepository applicantRepo) {
		this.answersRepo = answersRepo;
		this.questionRepository = questionRepository;
		this.formsRepo = formsRepo;
		this.applicantRepo = applicantRepo;
	}

	public String saveFeedback(Long applicantId, Long feedbackFormId, List<SaveFeedbackAnswerDTO> answers) {
		try {
			logger.info("Serive: Saving feedback | applicantId={} | feedbackFormId={} | answersCount={}", applicantId,
					feedbackFormId, answers.size());

			FeedbackFormsNew form = formsRepo.findByIdAndIsActiveTrue(feedbackFormId).orElseThrow(() -> {
				logger.warn("Service: Feedback form not found or inactive | formId={}", feedbackFormId);
				return new CustomException("Form not found or inactive", HttpStatus.BAD_REQUEST);
			});

			Applicant applicant = applicantRepo.findById(applicantId).orElseThrow(() -> {
				logger.warn("Service: Applicant not found | applicantId={}", applicantId);
				return new CustomException("Applicant not found", HttpStatus.NOT_FOUND);
			});

			answersRepo.findByFeedbackForm_IdAndApplicant_Id(feedbackFormId, applicantId).ifPresent(existing -> {
				logger.warn("Service: Duplicate feedback submission attempt | applicantId={} | feedbackFormId={}",
						applicantId, feedbackFormId);
				throw new CustomException("Feedback already submitted for this form by the applicant",
						HttpStatus.BAD_REQUEST);
			});

			validateAllRequiredQuestionsPresent(feedbackFormId, answers);

			FeedbackFormResponsesNew response = new FeedbackFormResponsesNew();
			response.setApplicant(applicant);
			response.setFeedbackForm(form);
			response.setSubmittedAt(LocalDateTime.now());
			mapAnswersToEntity(response, answers);
			answersRepo.save(response);
			form.setSubmissionCount(form.getSubmissionCount() + 1);
			formsRepo.save(form);

			logger.info("Service: Feedback saved successfully | applicantId={} | feedbackFormId={}", applicantId,
					feedbackFormId);
		} catch (CustomException e) {
			logger.error("Service: CustomException occurred | message={} | applicantId={} | feedbackFormId={}",
					e.getMessage(), applicantId, feedbackFormId);
			throw e;
		} catch (Exception e) {
			logger.error("Service: Exception occurred | message={} | applicantId={} | feedbackFormId={}",
					e.getMessage(), applicantId, feedbackFormId);
			throw new CustomException("An error occurred while saving feedback", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return "Feedback saved successfully";
	}

	private void mapAnswersToEntity(FeedbackFormResponsesNew response, List<SaveFeedbackAnswerDTO> answers) {

		for (SaveFeedbackAnswerDTO dto : answers) {
			String key = dto.getQuestionKey();
			String value = dto.getAnswer();

			logger.debug("Service: Processing feedback answer | key={} | value={}", key, value);

			switch (key) {

			case "communication":
				response.setCommunication(parseRating(value, key));
				break;

			case "concept_clarity":
				response.setConceptClarity(parseRating(value, key));
				break;

			case "confidence":
				response.setConfidence(parseRating(value, key));
				break;

			case "doubt_handling":
				response.setDoubtHandling(parseRating(value, key));
				break;

			case "hands_on":
				response.setHandsOn(parseRating(value, key));
				break;

			case "industry_relevance":
				response.setIndustryRelevance(parseRating(value, key));
				break;

			case "interaction":
				response.setInteraction(parseRating(value, key));
				break;

			case "overall_value":
				response.setOverallValue(parseRating(value, key));
				break;

			case "real_world_examples":
				response.setRealWorldExamples(parseRating(value, key));
				break;

			case "recommendation":
				response.setRecommendation(parseRating(value, key));
				break;

			case "session_structure":
				response.setSessionStructure(parseRating(value, key));
				break;

			case "timeliness":
				response.setTimeliness(parseRating(value, key));
				break;

			case "positive_feedback":
				response.setPositiveFeedback(value);
				break;

			case "improvement_feedback":
				response.setImprovementFeedback(value);
				break;

			default:
				logger.error("Service: Unsupported question key received: {}", key);
				throw new CustomException("Unsupported question key: " + key, HttpStatus.BAD_REQUEST);
			}
		}
	}

	private Integer parseRating(String answer, String questionKey) {
		try {
			int rating = Integer.parseInt(answer);
			if (rating < 1 || rating > 5) {
				logger.warn("Service: Invalid rating range | key={} | value={}", questionKey, answer);
				throw new CustomException("Rating must be between 1 and 5 for: " + questionKey, HttpStatus.BAD_REQUEST);
			}
			return rating;
		} catch (NumberFormatException ex) {
			logger.warn("Service: Non-numeric rating | key={} | value={}", questionKey, answer);
			throw new CustomException("Answer must be numeric for rating question: " + questionKey,
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validateAllRequiredQuestionsPresent(Long feedbackFormId, List<SaveFeedbackAnswerDTO> answers) {

		logger.info("Service: Validating required questions for feedback form | formId={}", feedbackFormId);
		List<String> expectedKeys = questionRepository.findRequiredQuestionKeys();

		Set<String> receivedKeys = answers.stream().map(SaveFeedbackAnswerDTO::getQuestionKey)
				.collect(Collectors.toSet());

		expectedKeys.removeAll(receivedKeys);

		if (!expectedKeys.isEmpty()) {
			logger.warn("Service: Missing required feedback answers | formId={} | missingKeys={}", feedbackFormId,
					expectedKeys);
			throw new CustomException("Missing required feedback answers for question keys: " + expectedKeys,
					HttpStatus.BAD_REQUEST);
		}
		logger.info("Service: All required questions are present for feedback form | formId={}", feedbackFormId);
	}

	public Object calculateRatingOfMentor(String mentorName, String collegeName, boolean category) {

		if (!formsRepo.existsByMentorNameIgnoreCase(mentorName)) {
			List<String> mentors = formsRepo.findAllDistinctMentorNames();
			throw new CustomException(
					"Mentor not found with given name: " + mentorName + ". Available mentors: " + mentors,
					HttpStatus.BAD_REQUEST);
		}

		List<FeedbackFormResponsesNew> responses;

		if (collegeName != null && !collegeName.isBlank()) {
			boolean collegeExists = formsRepo.existsByMentorNameAndCollegeNameIgnoreCase(mentorName, collegeName);
			if (!collegeExists) {
				List<String> colleges = formsRepo.findDistinctCollegesByMentor(mentorName);
				throw new CustomException("College with name: " + collegeName + " not found for mentor: " + mentorName
						+ ". Available colleges: " + colleges, HttpStatus.BAD_REQUEST);
			}
			responses = answersRepo.findByMentorNameAndCollegeName(mentorName, collegeName);

		} else {
			responses = answersRepo.findByMentorName(mentorName);
		}

		if (responses == null || responses.isEmpty()) {
			return category ? Collections.emptyMap() : 0.0;
		}

		if (!category) {
			return calculateAverage(responses);
		}

		Map<String, Double> categoryRatings = new LinkedHashMap<>();

		for (RatingCategory ratingCategory : RatingCategory.values()) {
			categoryRatings.put(ratingCategory.name(), calculateCategoryAverage(responses, ratingCategory));
		}

		return categoryRatings;
	}

	private double calculateAverage(List<FeedbackFormResponsesNew> responses) {

		if (responses == null || responses.isEmpty()) {
			return 0.0;
		}
		double totalScore = 0.0;
		int totalRatingsCount = 0;
		for (FeedbackFormResponsesNew r : responses) {

			if (r.getCommunication() != null) {
				totalScore += r.getCommunication();
				totalRatingsCount++;
			}
			if (r.getConceptClarity() != null) {
				totalScore += r.getConceptClarity();
				totalRatingsCount++;
			}
			if (r.getConfidence() != null) {
				totalScore += r.getConfidence();
				totalRatingsCount++;
			}
			if (r.getDoubtHandling() != null) {
				totalScore += r.getDoubtHandling();
				totalRatingsCount++;
			}
			if (r.getHandsOn() != null) {
				totalScore += r.getHandsOn();
				totalRatingsCount++;
			}
			if (r.getIndustryRelevance() != null) {
				totalScore += r.getIndustryRelevance();
				totalRatingsCount++;
			}
			if (r.getInteraction() != null) {
				totalScore += r.getInteraction();
				totalRatingsCount++;
			}
			if (r.getOverallValue() != null) {
				totalScore += r.getOverallValue();
				totalRatingsCount++;
			}
			if (r.getRealWorldExamples() != null) {
				totalScore += r.getRealWorldExamples();
				totalRatingsCount++;
			}
			if (r.getRecommendation() != null) {
				totalScore += r.getRecommendation();
				totalRatingsCount++;
			}
			if (r.getSessionStructure() != null) {
				totalScore += r.getSessionStructure();
				totalRatingsCount++;
			}
			if (r.getTimeliness() != null) {
				totalScore += r.getTimeliness();
				totalRatingsCount++;
			}
		}
		return totalRatingsCount == 0 ? 0.0 : Math.round((totalScore / totalRatingsCount) * 100.0) / 100.0;
	}

	private double calculateCategoryAverage(List<FeedbackFormResponsesNew> responses, RatingCategory category) {

		double totalScore = 0.0;
		int totalCount = 0;

		for (FeedbackFormResponsesNew r : responses) {
			for (String field : category.getFields()) {
				Integer value = getRatingValue(r, field);
				if (value != null) {
					totalScore += value;
					totalCount++;
				}
			}
		}

		return totalCount == 0 ? 0.0 : Math.round((totalScore / totalCount) * 100.0) / 100.0;
	}

	private Integer getRatingValue(FeedbackFormResponsesNew r, String field) {
		switch (field) {
		case "communication":
			return r.getCommunication();
		case "conceptClarity":
			return r.getConceptClarity();
		case "confidence":
			return r.getConfidence();
		case "doubtHandling":
			return r.getDoubtHandling();
		case "handsOn":
			return r.getHandsOn();
		case "industryRelevance":
			return r.getIndustryRelevance();
		case "interaction":
			return r.getInteraction();
		case "overallValue":
			return r.getOverallValue();
		case "realWorldExamples":
			return r.getRealWorldExamples();
		case "recommendation":
			return r.getRecommendation();
		case "sessionStructure":
			return r.getSessionStructure();
		case "timeliness":
			return r.getTimeliness();
		default:
			return null;
		}
	}

}
