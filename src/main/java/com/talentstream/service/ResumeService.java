package com.talentstream.service;
import org.springframework.stereotype.Service;

import com.talentstream.dto.ApplicantFullDataDTO;
import com.talentstream.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public ApplicantFullDataDTO getFullApplicant(Long applicantId) {
    	
        return resumeRepository.findFullApplicantData(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
       
    }
}
