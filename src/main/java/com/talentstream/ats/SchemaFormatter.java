package com.talentstream.ats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.dto.ApplicantFullDataDTO;
import com.talentstream.dto.ResumeSchemaDTO;

@Component
public class SchemaFormatter {

	public ResumeSchemaDTO format(ApplicantFullDataDTO dto) {

		// ===== HEADER =====
		String header = dto.getFirstName() + " | " +
				dto.getTitle() + " | " +
				dto.getEmail() + " | " +
				dto.getAlternatePhoneNumber();

		ResumeSchemaDTO resume = new ResumeSchemaDTO();
		resume.setHeader(header);

		List<ResumeSchemaDTO.Section> sections = new ArrayList<>();

		// ===== SUMMARY =====
		if (dto.getSummary() != null && !dto.getSummary().isBlank()) {
			sections.add(section("SUMMARY", List.of(esc(dto.getSummary()))));
		}
		// ===== SKILLS =====
		if (dto.getSkillsJson() != null && !dto.getSkillsJson().isBlank()) {
			sections.add(section("SKILLS", extractKeyWords(dto.getSkillsJson())));

		}

		// ===== SKILLS FROM PROJECT JSON =====
		List<String> skills = extractSkillsFromProjects(esc(dto.getProjectsJson()));
		if (!skills.isEmpty()) {
			sections.add(section("SKILLS", skills));
		}
		// ===== PROJECTS =====
		List<String> projectLines = extractProjectLines(dto.getProjectsJson());
		if (!projectLines.isEmpty()) {
			sections.add(section("PROJECTS", projectLines));
		}

		// ===== EDUCATION =====
		List<String> educationLines = new ArrayList<>();
		educationLines.add(
				"Graduation: " +
						dto.getGradDegree() + ", " +
						esc(dto.getGradSpecialization()) + " | " +
						dto.getGradUniversity() + " (" +
						dto.getGradStartYear() + " - " +
						dto.getGradEndYear() + ") | " +
						dto.getGradMarksPercent() + "%");
		educationLines.add(
				"Class XII: " +
						dto.getXiiBoard() + " | " +
						dto.getXiiPassingYear() + " | " +
						dto.getXiiMarksPercent() + "%");
		educationLines.add(
				"Class X: " +
						dto.getxBoard() + " | " +
						dto.getxPassingYear() + " | " +
						dto.getxMarksPercent() + "%");
		sections.add(section("EDUCATION", educationLines));

		// ===== KNOWN LANGUAGES =====
		if (dto.getKnownLanguagesJson() != null && !dto.getKnownLanguagesJson().isEmpty()) {
			sections.add(section(
					"KNOWN LANGUAGES",
					extractKeyWords(dto.getKnownLanguagesJson())));
		}

		resume.setSections(sections);

		return resume;
	}

	private ResumeSchemaDTO.Section section(String title, List<String> lines) {
		ResumeSchemaDTO.Section s = new ResumeSchemaDTO.Section();
		s.setTitle(title);
		s.setLines(lines);
		return s;
	}

	// extract skill strings from JSON
	private List<String> extractSkillsFromProjects(String projectsJson) {
		List<String> skills = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> projectList = mapper.readValue(
					projectsJson,
					new TypeReference<List<Map<String, Object>>>() {
					});
			for (Map<String, Object> p : projectList) {
				Object s = p.get("skills");
				if (s != null)
					skills.add(s.toString());
			}
		} catch (Exception ignore) {
		}
		return skills;
	}

	// extract project lines
	private List<String> extractProjectLines(String projectsJson) {
		List<String> lines = new ArrayList<>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> projectList = mapper.readValue(
					projectsJson,
					new TypeReference<List<Map<String, Object>>>() {
					});
			for (Map<String, Object> p : projectList) {
				lines.add(p.get("title") + " | " + p.get("technologies"));
				lines.add(String.valueOf(p.get("description")));
			}
		} catch (Exception ignore) {
		}
		return lines;
	}

	private String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	private List<String> extractKeyWords(String skillsJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(skillsJson, new TypeReference<List<String>>() {
			});
		} catch (Exception e) {
			return List.of();
		}
	}

}
