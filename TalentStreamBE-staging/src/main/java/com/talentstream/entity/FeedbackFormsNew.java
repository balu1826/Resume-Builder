package com.talentstream.entity;

import javax.persistence.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "feedback_forms_new")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class FeedbackFormsNew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "college_name", nullable = false)
    private String collegeName;

    @Column(name = "mentor_name", nullable = false)
    private String mentorName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "extra_questions")
    private Map<String, Object> extraQuestions;

    @Column(name = "title", nullable = false)
    private String title;

    // Relation to job_recruiter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_feedback_recruiter"))
    private JobRecruiter recruiter;

    @Column(name = "submission_count", nullable = false)
    private Integer submissionCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Map<String, Object> getExtraQuestions() { return extraQuestions; }
    public void setExtraQuestions(Map<String, Object> extraQuestions) { this.extraQuestions = extraQuestions; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public JobRecruiter getRecruiter() { return recruiter; }
    public void setRecruiter(JobRecruiter recruiter) { this.recruiter = recruiter; }

    public Integer getSubmissionCount() { return submissionCount; }
    public void setSubmissionCount(Integer submissionCount) { this.submissionCount = submissionCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
	
}
