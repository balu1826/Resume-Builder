package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talentstream.dto.CreateNewFeedBackFormQuestionDTO;
import com.talentstream.entity.FixedFeedBackFormQuestions;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.FixedFeedBackFormQuestionsRepository;

@Service
public class FixedFeedBackFormQuestionsService {

	private static final Logger logger = LoggerFactory.getLogger(FixedFeedBackFormQuestionsService.class);

	private final FixedFeedBackFormQuestionsRepository feedbackFormQuestionRepository;

	public FixedFeedBackFormQuestionsService(FixedFeedBackFormQuestionsRepository feedbackFormQuestionRepository) {
		this.feedbackFormQuestionRepository = feedbackFormQuestionRepository;
	}

	public void createFeedbackFormQuestion(CreateNewFeedBackFormQuestionDTO questionDTO) {
		try {
			logger.info("Creating fixed feedback question | key={} | category={}", questionDTO.getQuestionKey(),
					questionDTO.getCategory());

			FixedFeedBackFormQuestions question = new FixedFeedBackFormQuestions();
			question.setQuestion(questionDTO.getQuestion());
			question.setQuestionType(questionDTO.getQuestionType());
			question.setDisplayType(questionDTO.getDisplayType());
			question.setOptions(questionDTO.getOptions());
			question.setQuestionKey(questionDTO.getQuestionKey());
			question.setCategory(questionDTO.getCategory());
			question.setIsRequired(questionDTO.getIsRequired());
			question.setCreatedAt(LocalDateTime.now());

			feedbackFormQuestionRepository.save(question);

			logger.info("Fixed feedback question created successfully | key={}", questionDTO.getQuestionKey());

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error creating fixed feedback question | key={}", questionDTO.getQuestionKey(), e);
			throw new CustomException("Failed to create feedback question"+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public FixedFeedBackFormQuestions getFeedbackFormQuestionById(Long id) {
		try {
			logger.info("Fetching fixed feedback question | id={}", id);

			return feedbackFormQuestionRepository.findById(id).orElseThrow(() -> {
				logger.warn("Fixed feedback question not found | id={}", id);
				return new CustomException("Fixed feedback question not found", HttpStatus.NOT_FOUND);
			});

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error fetching fixed feedback question | id={}", id, e);
			throw new CustomException("Failed to fetch feedback question "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteFeedbackFormQuestion(Long id) {
		try {
			logger.info("Deleting fixed feedback question | id={}", id);

			if (!feedbackFormQuestionRepository.existsById(id)) {
				logger.warn("Attempt to delete non-existing question | id={}", id);
				throw new CustomException("Fixed feedback question not found", HttpStatus.NOT_FOUND);
			}

			feedbackFormQuestionRepository.deleteById(id);

			logger.info("Fixed feedback question deleted successfully | id={}", id);

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error deleting fixed feedback question | id={}", id, e);
			throw new CustomException("Failed to delete feedback question "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<CreateNewFeedBackFormQuestionDTO> getAllFixedQuestions() {
		try {
			logger.info("Fetching all fixed feedback questions");

			List<FixedFeedBackFormQuestions> entities = feedbackFormQuestionRepository.findAllByOrderByIdAsc();

			List<CreateNewFeedBackFormQuestionDTO> dtos = new ArrayList<>();

			for (FixedFeedBackFormQuestions entity : entities) {
				CreateNewFeedBackFormQuestionDTO dto = new CreateNewFeedBackFormQuestionDTO();

				dto.setQuestion(entity.getQuestion());
				dto.setQuestionType(entity.getQuestionType());
				dto.setDisplayType(entity.getDisplayType());
				dto.setOptions(entity.getOptions());
				dto.setQuestionKey(entity.getQuestionKey());
				dto.setCategory(entity.getCategory());
				dto.setIsRequired(entity.getIsRequired());

				dtos.add(dto);
			}

			logger.info("Retrieved fixed feedback questions successfully | count={}", dtos.size());

			return dtos;

		} catch (Exception e) {
			logger.error("Error fetching all fixed feedback questions", e);
			throw new CustomException("Failed to fetch feedback questions "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
