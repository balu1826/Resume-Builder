package com.talentstream.dto;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.exception.CustomException;

public class StreakQuestionsDTO {

	private String question;
	private String description;
	private Map<String, String> options;
	private String correctAnswer;
	private LocalDate date;

	// Constructors
	public StreakQuestionsDTO() {
	}

	public StreakQuestionsDTO(String question, String description, String correctAnswer, String optionsJson,
			LocalDate date, ObjectMapper mapper) {

		this.question = question;
		this.description = description;
		this.correctAnswer = correctAnswer;
		this.setDate(date);

		try {
			this.options = mapper.readValue(optionsJson, new TypeReference<Map<String, String>>() {
			});
		} catch (Exception e) {
			throw new CustomException("Failed to parse options JSON", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public StreakQuestionsDTO(String question, String correctAnswer, String optionsJson) {

		this.question = question;
		this.correctAnswer = correctAnswer;

		try {
			this.options = new ObjectMapper().readValue(optionsJson, new TypeReference<Map<String, String>>() {
			});
		} catch (Exception e) {
			throw new CustomException("Failed to parse options JSON", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public StreakQuestionsDTO(String question, String description, String correctAnswer, String optionsJson) {

		this.question = question;
		this.description = description;
		this.correctAnswer = correctAnswer;

		try {
			this.options = new ObjectMapper().readValue(optionsJson,
					new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {
					});
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse options JSON", e);
		}
	}

	// Getters & Setters
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}
