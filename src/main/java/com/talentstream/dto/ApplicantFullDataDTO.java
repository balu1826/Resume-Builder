package com.talentstream.dto;
public class ApplicantFullDataDTO {

    private Long applicantId;
    private String email;
    private String title;
    private String summary;

    private Long profileId;
    private String firstName;
    private String gender;
    private String alternatePhoneNumber;
    private String dateOfBirth;
    private String experience;
    private String qualification;
    private String specialization;
    private String city;

    private String gradDegree;
    private String gradCourse;
    private String gradSpecialization;
    private String gradUniversity;
    private Integer gradStartYear;
    private Integer gradEndYear;
    private Double gradMarksPercent;
    private String gradGradingSystem;

    private String xBoard;
    private Double xMarksPercent;
    private Integer xPassingYear;

    private String xiiBoard;
    private Double xiiMarksPercent;
    private Integer xiiPassingYear;
    
    private String skillsJson;

    private String projectsJson;
    
    private String knownLanguagesJson;


    public ApplicantFullDataDTO(
        Long applicantId, String email, String title, String summary,
        Long profileId, String firstName, String gender, String alternatePhoneNumber, String dateOfBirth,
        String experience, String qualification, String specialization, String city,
        String gradDegree, String gradCourse, String gradSpecialization, String gradUniversity,
        Integer gradStartYear, Integer gradEndYear, Double gradMarksPercent, String gradGradingSystem,
        String xBoard, Double xMarksPercent, Integer xPassingYear,
        String xiiBoard, Double xiiMarksPercent, Integer xiiPassingYear,
        String skillsJson,
        String projectsJson,
        String  knownLanguagesJson
    ) {
        this.applicantId = applicantId;
        this.email = email;
        this.title = title;
        this.summary = summary;

        this.profileId = profileId;
        this.firstName = firstName;
        this.gender = gender;
        this.alternatePhoneNumber = alternatePhoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.experience = experience;
        this.qualification = qualification;
        this.specialization = specialization;
        this.city = city;

        this.gradDegree = gradDegree;
        this.gradCourse = gradCourse;
        this.gradSpecialization = gradSpecialization;
        this.gradUniversity = gradUniversity;
        this.gradStartYear = gradStartYear;
        this.gradEndYear = gradEndYear;
        this.gradMarksPercent = gradMarksPercent;
        this.gradGradingSystem = gradGradingSystem;

        this.xBoard = xBoard;
        this.xMarksPercent = xMarksPercent;
        this.xPassingYear = xPassingYear;

        this.xiiBoard = xiiBoard;
        this.xiiMarksPercent = xiiMarksPercent;
        this.xiiPassingYear = xiiPassingYear;
        
        this.skillsJson=skillsJson;

        this.projectsJson = projectsJson;
        this. knownLanguagesJson= knownLanguagesJson;
    }

	public Long getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(Long applicantId) {
		this.applicantId = applicantId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAlternatePhoneNumber() {
		return alternatePhoneNumber;
	}

	public void setAlternatePhoneNumber(String alternatePhoneNumber) {
		this.alternatePhoneNumber = alternatePhoneNumber;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getGradDegree() {
		return gradDegree;
	}

	public void setGradDegree(String gradDegree) {
		this.gradDegree = gradDegree;
	}

	public String getGradCourse() {
		return gradCourse;
	}

	public void setGradCourse(String gradCourse) {
		this.gradCourse = gradCourse;
	}

	public String getGradSpecialization() {
		return gradSpecialization;
	}

	public void setGradSpecialization(String gradSpecialization) {
		this.gradSpecialization = gradSpecialization;
	}

	public String getGradUniversity() {
		return gradUniversity;
	}

	public void setGradUniversity(String gradUniversity) {
		this.gradUniversity = gradUniversity;
	}

	public Integer getGradStartYear() {
		return gradStartYear;
	}

	public void setGradStartYear(Integer gradStartYear) {
		this.gradStartYear = gradStartYear;
	}

	public Integer getGradEndYear() {
		return gradEndYear;
	}

	public void setGradEndYear(Integer gradEndYear) {
		this.gradEndYear = gradEndYear;
	}

	public Double getGradMarksPercent() {
		return gradMarksPercent;
	}

	public void setGradMarksPercent(Double gradMarksPercent) {
		this.gradMarksPercent = gradMarksPercent;
	}

	public String getGradGradingSystem() {
		return gradGradingSystem;
	}

	public void setGradGradingSystem(String gradGradingSystem) {
		this.gradGradingSystem = gradGradingSystem;
	}

	public String getxBoard() {
		return xBoard;
	}

	public void setxBoard(String xBoard) {
		this.xBoard = xBoard;
	}

	public Double getxMarksPercent() {
		return xMarksPercent;
	}

	public void setxMarksPercent(Double xMarksPercent) {
		this.xMarksPercent = xMarksPercent;
	}

	public Integer getxPassingYear() {
		return xPassingYear;
	}

	public void setxPassingYear(Integer xPassingYear) {
		this.xPassingYear = xPassingYear;
	}

	public String getXiiBoard() {
		return xiiBoard;
	}

	public void setXiiBoard(String xiiBoard) {
		this.xiiBoard = xiiBoard;
	}

	public Double getXiiMarksPercent() {
		return xiiMarksPercent;
	}

	public void setXiiMarksPercent(Double xiiMarksPercent) {
		this.xiiMarksPercent = xiiMarksPercent;
	}

	public Integer getXiiPassingYear() {
		return xiiPassingYear;
	}

	public void setXiiPassingYear(Integer xiiPassingYear) {
		this.xiiPassingYear = xiiPassingYear;
	}
	

	public String getSkillsJson() {
		return skillsJson;
	}

	public void setSkillsJson(String skillsJson) {
		this.skillsJson = skillsJson;
	}

	public String getProjectsJson() {
		return projectsJson;
	}

	public void setProjectsJson(String projectsJson) {
		this.projectsJson = projectsJson;
	}

	public String getKnownLanguagesJson() {
		return knownLanguagesJson;
	}

	public void setKnownLanguagesJson(String knownLanguagesJson) {
		this.knownLanguagesJson = knownLanguagesJson;
	}
	

    
}
