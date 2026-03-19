package com.talentstream.dto;

import java.util.List;

import com.talentstream.entity.FeedbackFormsNew;

public class GetFeedbackFormByIdDTO {

    private Long feedbackFormId;
    private String mentorName;
    private String collegeName;
    private String formName;
    private String description;
    private Boolean isActive;
    private List<CreateNewFeedBackFormQuestionDTO> questions;

    public GetFeedbackFormByIdDTO(FeedbackFormsNew feedbackForm, List<CreateNewFeedBackFormQuestionDTO> list) {
        this.feedbackFormId = feedbackForm.getId();
        this.mentorName = feedbackForm.getMentorName();
        this.collegeName = feedbackForm.getCollegeName();
        this.formName = feedbackForm.getTitle();
        this.description = feedbackForm.getDescription();
        this.isActive = feedbackForm.getIsActive();
        this.questions = list;
    }

    // Getters and setters
    public Long getFeedbackFormId() {
        return feedbackFormId;
    }

    public void setFeedbackFormId(Long feedbackFormId) {
        this.feedbackFormId = feedbackFormId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
	}

	public List<CreateNewFeedBackFormQuestionDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<CreateNewFeedBackFormQuestionDTO> questions) {
		this.questions = questions;
	}
    
}