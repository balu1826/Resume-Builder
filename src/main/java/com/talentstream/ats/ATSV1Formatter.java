package com.talentstream.ats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ATSV1Formatter implements ATSFormatter {

    @Override
    public ResumeSchemaDTO format(ATSResumeProfileDTO profile) {
    	BasicDetailsDTO basic = profile.getBasicDetails();
    	String header =
    	        profile.getName() + " | " +
    	        profile.getTitle() + " | " +
    	        basic.getEmail() + " | " +
    	        basic.getAlternatePhoneNumber();

    	ResumeSchemaDTO resume = new ResumeSchemaDTO();
    	resume.setHeader(header);

        List<ResumeSchemaDTO.Section> sections = new ArrayList<>();

        sections.add(section("SUMMARY",
                List.of(profile.getSummary())));

        sections.add(section("SKILLS",
                profile.getSkills()));

        EducationDetailsDTO edu = profile.getEducation();

        List<String> educationLines = new ArrayList<>();

        // Graduation
        EducationDetailsDTO.GraduationDTO grad = edu.getGraduation();
        educationLines.add(
            "Graduation: " +
            grad.getDegree() + ", " +
            grad.getSpecialization() + " | " +
            grad.getUniversity() + " (" +
            grad.getStartYear() + " - " +
            grad.getEndYear() + ") | " +
            grad.getMarksPercent() + "%"
        );

        // Class XII
        EducationDetailsDTO.ClassXiiDTO xii = edu.getClassXii();
        educationLines.add(
            "Class XII: " +
            xii.getBoard() + " | " +
            xii.getPassingYear() + " | " +
            xii.getMarksPercent() + "%"
        );

        // Class X
        EducationDetailsDTO.ClassXDTO x = edu.getClassX();
        educationLines.add(
            "Class X: " +
            x.getBoard() + " | " +
            x.getPassingYear() + " | " +
            x.getMarksPercent() + "%"
        );

        sections.add(section("EDUCATION", educationLines));

        //Project Lines
        List<String> projectLines = new ArrayList<>();
        for (ProjectDetailsDTO p : profile.getProjects()) {
            projectLines.add(
                p.getProjectTitle() + " | " +
                p.getTechnologiesUsed()
            );
            projectLines.add(
                    p.getProjectDescription()
                );
        }
        sections.add(section("PROJECTS", projectLines));
        
//        if (profile.getKnownLanguages() != null && !profile.getKnownLanguages().isEmpty()) {
//            sections.add(section(
//                "KNOWN LANGUAGES",
//                profile.getKnownLanguages()
//            ));
//        }

        resume.setSections(sections);
        return resume;
    }
    @Override
    public ResumeSchemaDTO format1(ApplicantFullDataDTO dto) {

        // ===== HEADER =====
        String header =
                dto.getFirstName() + " | " +
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

        // ===== EDUCATION =====
        List<String> educationLines = new ArrayList<>();
        educationLines.add(
                "Graduation: " +
                dto.getGradDegree() + ", " +
               esc( dto.getGradSpecialization() )+ " | " +
                dto.getGradUniversity() + " (" +
                dto.getGradStartYear() + " - " +
                dto.getGradEndYear() + ") | " +
                dto.getGradMarksPercent() + "%"
        );
        educationLines.add(
                "Class XII: " +
                dto.getXiiBoard() + " | " +
                dto.getXiiPassingYear() + " | " +
                dto.getXiiMarksPercent() + "%"
        );
        educationLines.add(
                "Class X: " +
                dto.getxBoard() + " | " +
                dto.getxPassingYear() + " | " +
                dto.getxMarksPercent() + "%"
        );
        sections.add(section("EDUCATION", educationLines));

        // ===== PROJECTS =====
        List<String> projectLines = extractProjectLines(dto.getProjectsJson());
        if (!projectLines.isEmpty()) {
            sections.add(section("PROJECTS", projectLines));
        }
        // ===== PROJECTS =====
        if (dto.getKnownLanguagesJson() != null && !dto.getKnownLanguagesJson().isEmpty()) {
         sections.add(section(
              "KNOWN LANGUAGES",
            extractKeyWords( dto.getKnownLanguagesJson())
          ));
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
                    new TypeReference<List<Map<String, Object>>>() {}
            );
            for (Map<String, Object> p : projectList) {
                Object s = p.get("skills");
                if (s != null) skills.add(s.toString());
            }
        } catch (Exception ignore) {}
        return skills;
    }

    // extract project lines
    private List<String> extractProjectLines(String projectsJson) {
        List<String> lines = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> projectList = mapper.readValue(
                    projectsJson,
                    new TypeReference<List<Map<String, Object>>>() {}
            );
            for (Map<String, Object> p : projectList) {
                lines.add(p.get("title") + " | " + p.get("technologies"));
                lines.add(String.valueOf(p.get("description")));
            }
        } catch (Exception ignore) {}
        return lines;
    }
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    private List<String> extractKeyWords(String skillsJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(skillsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

}
