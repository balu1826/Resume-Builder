package com.talentstream.entity;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import java.time.LocalDateTime;

import java.util.List;

@Entity

@Table(name = "fixed_feedback_form_questions")

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)

public class FixedFeedBackFormQuestions {

	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	@Column(name = "question_key", nullable = false, unique = true)
	private String questionKey;

	@Column(name = "question", nullable = false, columnDefinition = "TEXT")
	private String question;

	@Column(name = "question_type", nullable = false)
	private String questionType;

	@Column(name = "display_type")
    private String displayType;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<String> options;

	@Column(name = "is_required")
	private Boolean isRequired;

	@Column(name = "category", nullable = false)
	private String category;

	@Column(name = "created_at", updatable = false)

	private LocalDateTime createdAt;

	public Long getId() {

		return id;

	}

	public void setId(Long id) {

		this.id = id;

	}

	public String getQuestionKey() {

		return questionKey;

	}

	public void setQuestionKey(String questionKey) {

		this.questionKey = questionKey;

	}

	public String getQuestion() {

		return question;

	}

	public void setQuestion(String question) {

		this.question = question;

	}

	public String getQuestionType() {

		return questionType;

	}

	public void setQuestionType(String questionType) {

		this.questionType = questionType;

	}

	public String getDisplayType() {

		return displayType;

	}

	public void setDisplayType(String displayType) {

		this.displayType = displayType;

	}

	public List<String> getOptions() {

		return options;

	}

	public void setOptions(List<String> options) {

		this.options = options;

	}

	public Boolean getIsRequired() {

		return isRequired;

	}

	public String getCategory() {

		return category;

	}

	public void setCategory(String category) {

		this.category = category;

	}

	public void setIsRequired(Boolean isRequired) {

		this.isRequired = isRequired;

	}

	public LocalDateTime getCreatedAt() {

		return createdAt;

	}

	public void setCreatedAt(LocalDateTime createdAt) {

		this.createdAt = createdAt;

	}

}
