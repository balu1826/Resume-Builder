package com.talentstream.entity;

import javax.persistence.*;

@NamedNativeQuery(
	    name = "ApplicantFullDataQuery",
	    query =
	        "SELECT " +
	        " a.id AS applicantId, " +
	        " a.email AS email, " +
	        " a.title AS title, " +
	        " a.summary AS summary, " +

	        " ap.profileid AS profileId, " +
	        " ap.first_name AS firstName, " +
	        " ap.gender AS gender, " +
	        " ap.alternate_phone_number AS alternatePhoneNumber, " +
	        " ap.date_of_birth AS dateOfBirth, " +
	        " ap.experience AS experience, " +
	        " ap.qualification AS qualification, " +
	        " ap.specialization AS specialization, " +
	        " ap.city AS city, " +

	        " ae.grad_degree AS gradDegree, " +
	        " ae.grad_course AS gradCourse, " +
	        " ae.grad_specialization AS gradSpecialization, " +
	        " ae.grad_university AS gradUniversity, " +
	        " ae.grad_start_year AS gradStartYear, " +
	        " ae.grad_end_year AS gradEndYear, " +
	        " ae.grad_marks_percent AS gradMarksPercent, " +
	        " ae.grad_grading_system AS gradGradingSystem, " +

	        " ae.x_board AS xBoard, " +
	        " ae.x_marks_percent AS xMarksPercent, " +
	        " ae.x_passing_year AS xPassingYear, " +

	        " ae.xii_board AS xiiBoard, " +
	        " ae.xii_marks_percent AS xiiMarksPercent, " +
	        " ae.xii_passing_year AS xiiPassingYear, " +
	        // Skills JSON
	        " COALESCE((SELECT jsonb_agg(to_jsonb(s.skill_name)) " +
	        "           FROM applicant_profile_skills_required apsr " +
	        "           JOIN applicant_skills s ON s.id = apsr.applicantskill_id " +
	        "           WHERE apsr.profileid = ap.profileid), CAST('[]' AS jsonb)) AS skillsJson ," +

	        " COALESCE(jsonb_agg(jsonb_build_object( " +
	        "     'project_id', pr.id, " +
	        "     'title', pr.project_title, " +
	        "     'description', pr.project_description, " +
	        "     'role', pr.role_in_project, " +
	        "     'role_description', pr.role_description, " +
	        "     'technologies', pr.technologies_used, " +
	        "     'skills', pr.skills_used, " +
	        "     'team_size', pr.team_size " +
	        " )) FILTER (WHERE pr.id IS NOT NULL), CAST('[]' AS jsonb)) AS projectsJson\r\n"
	        + " " +

	        "FROM applicant a " +
	        "LEFT JOIN applicant_profile ap ON ap.applicantid = a.id " +
	        "LEFT JOIN applicant_education ae ON ae.applicant_id = a.id " +
	        "LEFT JOIN applicant_projects pr ON pr.applicant_id = a.id " +
	       
	        "WHERE a.id = ?1 " +
	        "GROUP BY a.id, ap.profileid, ae.id",
	    resultSetMapping = "ApplicantFullDataMapping"
	)

@SqlResultSetMapping(
	    name = "ApplicantFullDataMapping",
	    classes = @ConstructorResult(
	        targetClass = com.talentstream.dto.ApplicantFullDataDTO.class,
	        columns = {
	            @ColumnResult(name = "applicantId", type = Long.class),
	            @ColumnResult(name = "email", type = String.class),
	            @ColumnResult(name = "title", type = String.class),
	            @ColumnResult(name = "summary", type = String.class),

	            @ColumnResult(name = "profileId", type = Long.class),
	            @ColumnResult(name = "firstName", type = String.class),
	            @ColumnResult(name = "gender", type = String.class),
	            @ColumnResult(name = "alternatePhoneNumber", type = String.class),
	            @ColumnResult(name = "dateOfBirth", type = String.class),
	            @ColumnResult(name = "experience", type = String.class),
	            @ColumnResult(name = "qualification", type = String.class),
	            @ColumnResult(name = "specialization", type = String.class),
	            @ColumnResult(name = "city", type = String.class),

	            @ColumnResult(name = "gradDegree", type = String.class),
	            @ColumnResult(name = "gradCourse", type = String.class),
	            @ColumnResult(name = "gradSpecialization", type = String.class),
	            @ColumnResult(name = "gradUniversity", type = String.class),
	            @ColumnResult(name = "gradStartYear", type = Integer.class),
	            @ColumnResult(name = "gradEndYear", type = Integer.class),
	            @ColumnResult(name = "gradMarksPercent", type = Double.class),
	            @ColumnResult(name = "gradGradingSystem", type = String.class),

	            @ColumnResult(name = "xBoard", type = String.class),
	            @ColumnResult(name = "xMarksPercent", type = Double.class),
	            @ColumnResult(name = "xPassingYear", type = Integer.class),

	            @ColumnResult(name = "xiiBoard", type = String.class),
	            @ColumnResult(name = "xiiMarksPercent", type = Double.class),
	            @ColumnResult(name = "xiiPassingYear", type = Integer.class),
	            
	            @ColumnResult(name = "skillsJson", type = String.class),
	            @ColumnResult(name = "projectsJson", type = String.class)
	          

	        }
	    )
	)
	@Entity
	@Table(name = "applicant_mapping_helper")
	public class ApplicantMappingHelper {

	    @Id
	    private Long id;

	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }
	}
