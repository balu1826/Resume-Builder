package com.talentstream.dto;


import java.util.List;

public class ATSResumeProfileDTO {

    // Derived / presentation-level
    private String name;       // firstName + lastName
    private String location;   // city + state

    // Profile level
    private String title;      // role
    private String summary;

    // Reused existing DTOs
    private BasicDetailsDTO basicDetails;
    private EducationDetailsDTO education;
    private List<ProjectDetailsDTO> projects;
    private List<String> skills;

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public BasicDetailsDTO getBasicDetails() { return basicDetails; }
    public void setBasicDetails(BasicDetailsDTO basicDetails) { this.basicDetails = basicDetails; }

    public EducationDetailsDTO getEducation() { return education; }
    public void setEducation(EducationDetailsDTO education) { this.education = education; }

    public List<ProjectDetailsDTO> getProjects() { return projects; }
    public void setProjects(List<ProjectDetailsDTO> projects) { this.projects = projects; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> list) { this.skills = list; }
}

