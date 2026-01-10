package com.talentstream.service;

import com.talentstream.dto.ProjectDetailsDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProject;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantProjectRepository;
import com.talentstream.repository.ApplicantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicantProjectService {

	private final ApplicantRepository applicantRepository;
	private final ApplicantProjectRepository applicantProjectRepository;
    private final ApplicantProfileRepository profileRepo;

	

	public ApplicantProjectService(ApplicantRepository applicantRepository,
			ApplicantProjectRepository projectRepository, ApplicantProfileRepository profileRepo) {
		this.applicantRepository = applicantRepository;
		this.applicantProjectRepository = projectRepository;
		this.profileRepo = profileRepo;
	}

	@Transactional(readOnly = true)
	public List<ProjectDetailsDTO> getApplicantProjects(Long applicantId) {
		try {
			applicantRepository.findById(applicantId)
					.orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));

			List<ApplicantProject> projects = applicantProjectRepository.findByApplicantId(applicantId);

			if (projects.isEmpty()) {
				throw new CustomException("No projects found for this applicant", HttpStatus.NOT_FOUND);
			}

			return projects.stream().map(this::toDTO).collect(Collectors.toList());

		} catch (CustomException e) {
			throw e;
		} catch (Exception ex) {
			throw new CustomException("Failed to fetch applicant projects", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ApplicantProjectService.java
	@Transactional
	public String saveApplicantProject(Long applicantId, ProjectDetailsDTO dto) {
		try {
			Applicant applicant = applicantRepository.findById(applicantId)
					.orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));

			// Get last updated project or create new
			ApplicantProject project = applicantProjectRepository.findTopByApplicantIdOrderByUpdatedAtDesc(applicantId)
					.orElseGet(() -> {
						ApplicantProject p = new ApplicantProject();
						p.setApplicant(applicant);
						return p;
					});

			project.setProjectTitle(dto.getProjectTitle());
			project.setSpecialization(dto.getSpecialization());
			project.setTechnologiesUsed(dto.getTechnologiesUsed());
			project.setTeamSize(dto.getTeamSize());
			project.setRoleInProject(dto.getRoleInProject());
			project.setRoleDescription(dto.getRoleDescription());
			project.setProjectDescription(dto.getProjectDescription());
			project.setUpdatedAt(OffsetDateTime.now());
			profileRepo.findByApplicantId(applicantId).setUpdatedAt(LocalDateTime.now());

			applicantProjectRepository.save(project);
			return "Project details updated successfully";

		} catch (CustomException e) {
			throw e;
		} catch (Exception ex) {
			throw new CustomException("Failed to save applicant project", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional(readOnly = true)
	public ProjectDetailsDTO getApplicantCurrentProject(Long applicantId) {
		try {
			applicantRepository.findById(applicantId)
					.orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));

			ApplicantProject project = applicantProjectRepository.findTopByApplicantIdOrderByUpdatedAtDesc(applicantId)
					.orElse(null);

			if (project == null) {
				throw new CustomException("No project found for this applicant", HttpStatus.NOT_FOUND);
			}

			return toDTO(project);

		} catch (CustomException e) {
			throw e;
		} catch (Exception ex) {
			throw new CustomException("Failed to fetch latest applicant project", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ProjectDetailsDTO toDTO(ApplicantProject project) {
		ProjectDetailsDTO dto = new ProjectDetailsDTO();
		dto.setProjectTitle(project.getProjectTitle());
		dto.setSpecialization(project.getSpecialization());
		dto.setTechnologiesUsed(project.getTechnologiesUsed());
		dto.setTeamSize(project.getTeamSize());
		dto.setRoleInProject(project.getRoleInProject());
		dto.setRoleDescription(project.getRoleDescription());
		dto.setProjectDescription(project.getProjectDescription());
		return dto;
	}
}
