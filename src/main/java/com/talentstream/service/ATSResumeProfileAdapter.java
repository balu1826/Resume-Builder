package com.talentstream.service;

import com.talentstream.dto.*;
import com.talentstream.entity.BasicDetails;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ATSResumeProfileAdapter {

    private final ApplicantProfileService applicantProfileService;
    private final ApplicantEducationService applicantEducationService;
    private final ApplicantProjectService applicantProjectService;
    private final ApplicantKeySkillsService applicantKeySkillsService;
    private final ApplicantSummaryService applicantSummaryService;

    public ATSResumeProfileAdapter(
            ApplicantProfileService applicantProfileService,
            ApplicantEducationService applicantEducationService,
            ApplicantProjectService applicantProjectService,
            ApplicantKeySkillsService applicantKeySkillsService,
            ApplicantSummaryService applicantSummaryService
    ) {
        this.applicantProfileService = applicantProfileService;
        this.applicantEducationService = applicantEducationService;
        this.applicantProjectService = applicantProjectService;
        this.applicantKeySkillsService = applicantKeySkillsService;
        this.applicantSummaryService = applicantSummaryService;
    }

    public ATSResumeProfileDTO build(long applicantId) {

        ApplicantProfileDTO profile =
                applicantProfileService.getApplicantProfileById(applicantId);

        BasicDetails basic = profile.getBasicDetails();
        BasicDetailsDTO basicDto=new BasicDetailsDTO();
        BeanUtils.copyProperties(basic, basicDto);
        

        ATSResumeProfileDTO dto = new ATSResumeProfileDTO();

        dto.setBasicDetails(basicDto);
        dto.setEducation(applicantEducationService.getApplicantEducationDetails(applicantId));
        dto.setProjects(applicantProjectService.getApplicantProjects(applicantId));
        dto.setSkills(applicantKeySkillsService.getSkills(applicantId));
        dto.setSummary(applicantSummaryService.getApplicantSummary(applicantId));

        dto.setTitle(profile.getRoles());
        dto.setName(basic.getFirstName() + " " + basic.getLastName());
        dto.setLocation(basic.getCity() + ", " + basic.getState());

        return dto;
    }
}
