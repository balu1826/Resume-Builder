package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talentstream.dto.CreateFeedbackFormsDto;
import com.talentstream.dto.FeedbackFormsResponseDTO;
import com.talentstream.dto.GetFeedbackFormByIdDTO;
import com.talentstream.entity.FeedbackFormsNew;
import com.talentstream.entity.JobRecruiter;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.FeedbackFormsNewRepository;
import com.talentstream.repository.JobRecruiterRepository;

@Service
public class FeedbackFormsNewService {
   
	private static final Logger logger = LoggerFactory.getLogger(FeedbackFormsNewService.class);

	private final FixedFeedBackFormQuestionsService fixedFeedbackFormQuestionsService;
    private final JobRecruiterRepository recruiterRepository;
    private final FeedbackFormsNewRepository feedbackFormsNewRepository;
    public FeedbackFormsNewService(FixedFeedBackFormQuestionsService fixedFeedbackFormQuestionsService, JobRecruiterRepository recruiterRepository, FeedbackFormsNewRepository feedbackFormsNewRepository) {
        this.fixedFeedbackFormQuestionsService = fixedFeedbackFormQuestionsService;
        this.recruiterRepository = recruiterRepository;
		this.feedbackFormsNewRepository = feedbackFormsNewRepository;
}

	public String createFeedbackForm(Long recruiterId, CreateFeedbackFormsDto dto) {
		logger.info("createFeedbackForm called for recruiterId={}", recruiterId);
		JobRecruiter recruiter = recruiterRepository.findById(recruiterId)
				.orElseThrow(() -> new CustomException("Recruiter not found", HttpStatus.NOT_FOUND));

		FeedbackFormsNew form = new FeedbackFormsNew();
		form.setCreatedAt(LocalDateTime.now());
		form.setRecruiter(recruiter);
		return saveOrUpdateForm(form, dto, true);
	}

	public String updateFeedbackForm(Long formId, CreateFeedbackFormsDto feedbackFormDto) {
		logger.info("updateFeedbackForm called for formId={}", formId);
		FeedbackFormsNew feedbackForm = feedbackFormsNewRepository.findById(formId)
				.orElseThrow(() -> {
					logger.warn("Feedback form not found for formId={}", formId);
					return new CustomException("Feedback form not found for formId=" + formId,
							HttpStatus.NOT_FOUND);
				});
		
		return saveOrUpdateForm(feedbackForm, feedbackFormDto, false);
	}

	private String saveOrUpdateForm(FeedbackFormsNew form, CreateFeedbackFormsDto dto, boolean isNew) {
		logger.debug("saveOrUpdateForm called for form (title={}), isNew={}", dto.getFormName(), isNew);
		try {
		form.setMentorName(dto.getMentorName());
		form.setCollegeName(dto.getCollegeName());
		form.setTitle(dto.getFormName());
		form.setDescription(dto.getDescription());
		form.setIsActive(dto.getIsActive());
		form.setUpdatedAt(LocalDateTime.now());
		feedbackFormsNewRepository.save(form);
		} catch (Exception e) {
			logger.error("Error saving feedback form (title={}) : {}", dto.getFormName(), e.getMessage());
			throw new CustomException("Error saving feedback form: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return isNew ? "Feedback form created successfully" : "Feedback form updated successfully";
	}

	public GetFeedbackFormByIdDTO getFeedbackFormById(Long formId) {
		logger.info("getFeedbackFormById called for formId={}", formId);
		FeedbackFormsNew feedbackForm = feedbackFormsNewRepository.findById(formId)
				.orElseThrow(() -> {
					logger.warn("Feedback form not found for formId={}", formId);
					return new CustomException("Feedback form not found for formId=" + formId,
							HttpStatus.NOT_FOUND);
				});

		return new GetFeedbackFormByIdDTO(feedbackForm, fixedFeedbackFormQuestionsService.getAllFixedQuestions());
	}

	public List<FeedbackFormsResponseDTO> getAllActiveFeedbackForms() {
		try {
			List<FeedbackFormsResponseDTO> forms = feedbackFormsNewRepository.findAllByIsActiveTrue();
			logger.info("Retrieved {} active forms", forms.size());
			return forms;
		} catch (Exception e) {
			logger.error("Error fetching all active forms: {}", e.getMessage(), e);
			throw new CustomException("Error fetching all active forms: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<FeedbackFormsResponseDTO> getAllFeedbackForms(Long recruiterId) {
		try {
			if(recruiterId != null) {
				List<FeedbackFormsResponseDTO> forms = feedbackFormsNewRepository.findAllByRecruiterId(recruiterId);
				logger.info("Retrieved {} forms for recruiterId={}", forms.size(), recruiterId);
				return forms;
			} else {
				List<FeedbackFormsResponseDTO> forms = feedbackFormsNewRepository.findAllForms();
				logger.info("Retrieved {} forms", forms.size());
				return forms;
			}
		} catch (Exception e) {
			logger.error("Error fetching forms for recruiterId={}: {}", recruiterId, e.getMessage(), e);
			throw new CustomException("Error fetching forms: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String deleteFeedbackFormById(Long formId) {
		logger.info("deleteFeedbackFormById called for formId={}", formId);
		FeedbackFormsNew feedbackForm = feedbackFormsNewRepository.findById(formId)
				.orElseThrow(() -> {
					logger.warn("Feedback form not found for formId={}", formId);
					return new CustomException("Feedback form not found for formId=" + formId,
							HttpStatus.NOT_FOUND);
				});
		try {
			feedbackFormsNewRepository.delete(feedbackForm);
			logger.info("Successfully deleted feedback form for ID: {}", formId);
			return "Feedback form deleted successfully";
		} catch (Exception e) {
			logger.error("Error deleting feedback form for ID: {}", formId, e);
			throw new CustomException("Error deleting feedback form: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Map<String, Set<String>> getAllMentorNamesAndCollegeNames() {

	    List<Object[]> rows =
	            feedbackFormsNewRepository.findAllMentorNamesAndCollegeNames();

	    Map<String, Set<String>> result = new HashMap<>();

	    for (Object[] row : rows) {
	        String mentor = (String) row[0];
	        String college = (String) row[1];

	        result
	            .computeIfAbsent(mentor, k -> new HashSet<>())
	            .add(college);
	    }

	    return result;
	}

}
