package com.talentstream.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talentstream.dto.ApplicantFullDataDTO;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public ApplicantFullDataDTO getFullApplicant(Long applicantId) {
    	 log.info("Fetching applicant data for ID: {}", applicantId);
    	  ApplicantFullDataDTO raw = resumeRepository.findFullApplicantData(applicantId)
    	            .orElseThrow(() -> new CustomException(
    	                    "Applicant not found with id: " + applicantId,
    	                    HttpStatus.NOT_FOUND
    	            ));
    	  log.info("Applicant data fetched successfully for ID: {}", applicantId);
    	  validateResume(raw);
    	  log.info("Applicant data validated successfully for ID: {}", applicantId);

    	    return raw;
       
    }
    private void validateResume(ApplicantFullDataDTO raw) {

        if (raw == null) {
            throw new CustomException("Applicant not found", HttpStatus.NOT_FOUND);
        }

        // Title validation
        if (raw.getTitle() == null || raw.getTitle().trim().isEmpty()) {
            throw new CustomException("Title is missing", HttpStatus.BAD_REQUEST);
        }

        // Education validation
        boolean isGradMissing =
                raw.getGradDegree() == null &&
                raw.getGradCourse() == null &&
                raw.getGradUniversity() == null;

        boolean isXMissing =
                raw.getxBoard() == null &&
                raw.getxMarksPercent() == null;

        boolean isXIIMissing =
                raw.getXiiBoard() == null &&
                raw.getXiiMarksPercent() == null;

        if (isGradMissing && isXMissing && isXIIMissing) {
            throw new CustomException(
                    "Education details are missing",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}