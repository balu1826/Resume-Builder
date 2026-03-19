package com.talentstream.dto;
 
import java.util.List;
 
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
 
import com.talentstream.validation.ValidFeedbackQuestion;
 
 
 
@ValidFeedbackQuestion
public class CreateNewFeedBackFormQuestionDTO {
 
	@NotBlank(message = "Question key is required")
	@Pattern(regexp = "^q_[a-z]+(_[a-z]+)*$", message = "Question key must follow format like q_concept_clarity")
	private String questionKey;
 
    @NotBlank(message = "Question is required")
    @Size(min = 5, max = 500, message = "Question must be between 5 and 500 characters")
	private String question;
 
    @NotBlank(message = "questionType is required.Refer to allowed enum values: RADIO, CHECKBOX, RATING, NUMBER, TEXTAREA, EMAIL, TEXT, PHONE")
	@Pattern(
	  regexp = "RADIO|CHECKBOX|RATING|NUMBER|TEXTAREA|EMAIL|TEXT|PHONE",
	  message = "Invalid questionType. Refer to allowed enum values: RADIO, CHECKBOX, RATING, NUMBER, TEXTAREA, EMAIL, TEXT, PHONE"
	)
	private String questionType;
   
    @Pattern(
	  regexp = "EMOJIS|STARS|SCALE",
	  message = "Invalid displayType. Refer to allowed enum values: EMOJIS, STARS, SCALE"
	)
	private String displayType;
	
    @Size(max = 20, message = "options must contain at most 20 entries")
	private List<@NotBlank(message = "option value cannot be blank") @Size(max = 255, message = "option must be at most 255 characters") String> options;
 
	private Boolean isRequired;
 
    @NotBlank(message = "Category is required")
	@Pattern(regexp = "TEACHING_EFFICIENCY|SKILL_READINESS|MENTOR_INTERACTION|DELIVERY_DESPLINE|OVERALL_PROGRAM_HEALTH|OTHER", message = "Invalid category. Refer to allowed enum values: TEACHING_EFFICIENCY, SKILL_READINESS, MENTOR_INTERACTION, DELIVERY_DESPLINE, OVERALL_PROGRAM_HEALTH, OTHER")
	private String category;
 
	// Getters and Setters
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
 
	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}
 
	public String getCategory() {
		return category;
	}
 
	public void setCategory(String category) {
		this.category = category;
	}
}