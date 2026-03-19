package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.ApplicantSkills;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantSkillsRepository;

@Service
public class ApplicantKeySkillsService {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantKeySkillsService.class);

	private final ApplicantProfileRepository profileRepo;
	private final ApplicantSkillsRepository skillRepo;

	public ApplicantKeySkillsService(ApplicantProfileRepository profileRepo, ApplicantSkillsRepository skillRepo) {
		this.profileRepo = profileRepo;
		this.skillRepo = skillRepo;
	}

	@Transactional(readOnly = true)
	public List<String> getSkills(long applicantId) {
		logger.debug("getApplicantSkills - applicantId={}", applicantId);
		ApplicantProfile p = profileRepo.findByApplicantId(applicantId);
		if (p == null) {
			logger.warn("getApplicantSkills - profile not found for applicantId={}", applicantId);
			throw new CustomException("Profile not found", HttpStatus.NOT_FOUND);
		}
		List<String> skills = p.getSkillsRequired().stream().map(ApplicantSkills::getSkillName)
				.sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
		logger.debug("getApplicantSkills - found {} skills for applicantId={}", skills.size(), applicantId);
		return skills;
	}

	@Transactional
	public String replaceApplicantSkills(long applicantId, List<String> skillNames) {

		logger.debug("replaceApplicantSkills - applicantId={}, skillCount={}", applicantId,
				skillNames == null ? 0 : skillNames.size());

		if (skillNames == null || skillNames.isEmpty()) {
			logger.warn("replaceApplicantSkills - no skills provided for applicantId={}", applicantId);
			throw new CustomException("At least one skill is required", HttpStatus.BAD_REQUEST);
		}
		 
		skillNames = skillNames.stream()
		            .filter(s -> s != null && !s.isBlank())
		            .map(String::trim)
		            .map(String::toLowerCase)
		            .distinct()
		            .collect(Collectors.toList());
		
		 ApplicantProfile profile = profileRepo.findByApplicantId(applicantId);
		    if (profile == null) {
		        throw new CustomException("Applicant profile not found", HttpStatus.NOT_FOUND);
		    }

		    List<ApplicantSkills> existingSkills = skillRepo.findBySkillNameIgnoreCaseIn(skillNames);

		    Map<String, ApplicantSkills> existingSkillMap = existingSkills.stream()
		            .collect(Collectors.toMap(
		                    s -> s.getSkillName().toLowerCase(),
		                    s -> s,
		                    (existing, duplicate) -> existing
		            ));
		    Set<ApplicantSkills> finalSkills = new HashSet<>();
		    List<ApplicantSkills> newSkillsToSave = new ArrayList<>();


		    for (String name : skillNames) {
		        ApplicantSkills skill = existingSkillMap.get(name);
		        if (skill == null) {
		            skill = new ApplicantSkills();
		            skill.setSkillName(name);
		            newSkillsToSave.add(skill);
		        }
		        finalSkills.add(skill);
		    }

		    if (!newSkillsToSave.isEmpty()) {
		        skillRepo.saveAll(newSkillsToSave);
		    }

		    profile.getSkillsRequired().clear();
		    profile.getSkillsRequired().addAll(finalSkills);
		    profile.setUpdatedAt(LocalDateTime.now());
		    profileRepo.save(profile);

		    return "Skill updated successfully";
	}

}
