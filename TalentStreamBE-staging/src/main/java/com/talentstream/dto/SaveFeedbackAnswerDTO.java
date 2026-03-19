package com.talentstream.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SaveFeedbackAnswerDTO {

    @NotBlank(message = "Answer must not be blank")
    private String answer;

    @NotBlank(message = "Question key is required")
    private String questionKey;

    @NotNull(message = "Question number is required")
    @Positive(message = "Question number must be positive")
    private Integer questionNumber;

    // getters & setters

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestionKey() {
        return questionKey;
    }

    public void setQuestionKey(String questionKey) {
        this.questionKey = questionKey;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }
}
