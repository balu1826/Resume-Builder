package com.talentstream.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_form_responses_new")
public class FeedbackFormResponsesNew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_at", nullable = true)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_feedback_applicant"))
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_form_id", nullable = false, foreignKey = @ForeignKey(name = "fk_feedback_forms"))
    private FeedbackFormsNew feedbackForm;

    // Feedback ratings
    @Column(name = "concept_clarity")
    private Integer conceptClarity;

    @Column(name = "real_world_examples")
    private Integer realWorldExamples;

    @Column(name = "session_structure")
    private Integer sessionStructure;

    @Column(name = "hands_on")
    private Integer handsOn;

    @Column(name = "industry_relevance")
    private Integer industryRelevance;

    @Column(name = "confidence")
    private Integer confidence;

    @Column(name = "interaction")
    private Integer interaction;

    @Column(name = "doubt_handling")
    private Integer doubtHandling;

    @Column(name = "timeliness")
    private Integer timeliness;

    @Column(name = "communication")
    private Integer communication;

    @Column(name = "overall_value")
    private Integer overallValue;

    @Column(name = "recommendation")
    private Integer recommendation;

    // Open text feedback
    @Column(name = "positive_feedback", columnDefinition = "TEXT")
    private String positiveFeedback;

    @Column(name = "improvement_feedback", columnDefinition = "TEXT")
    private String improvementFeedback;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public FeedbackFormsNew getFeedbackForm() {
        return feedbackForm;
    }

    public void setFeedbackForm(FeedbackFormsNew feedbackForm) {
        this.feedbackForm = feedbackForm;
    }

    public Integer getConceptClarity() {
        return conceptClarity;
    }

    public void setConceptClarity(Integer conceptClarity) {
        this.conceptClarity = conceptClarity;
    }

    public Integer getRealWorldExamples() {
        return realWorldExamples;
    }

    public void setRealWorldExamples(Integer realWorldExamples) {
        this.realWorldExamples = realWorldExamples;
    }

    public Integer getSessionStructure() {
        return sessionStructure;
    }

    public void setSessionStructure(Integer sessionStructure) {
        this.sessionStructure = sessionStructure;
    }

    public Integer getHandsOn() {
        return handsOn;
    }

    public void setHandsOn(Integer handsOn) {
        this.handsOn = handsOn;
    }

    public Integer getIndustryRelevance() {
        return industryRelevance;
    }

    public void setIndustryRelevance(Integer industryRelevance) {
        this.industryRelevance = industryRelevance;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public Integer getInteraction() {
        return interaction;
    }

    public void setInteraction(Integer interaction) {
        this.interaction = interaction;
    }

    public Integer getDoubtHandling() {
        return doubtHandling;
    }

    public void setDoubtHandling(Integer doubtHandling) {
        this.doubtHandling = doubtHandling;
    }

    public Integer getTimeliness() {
        return timeliness;
    }

    public void setTimeliness(Integer timeliness) {
        this.timeliness = timeliness;
    }

    public Integer getCommunication() {
        return communication;
    }

    public void setCommunication(Integer communication) {
        this.communication = communication;
    }

    public Integer getOverallValue() {
        return overallValue;
    }

    public void setOverallValue(Integer overallValue) {
        this.overallValue = overallValue;
    }

    public Integer getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Integer recommendation) {
        this.recommendation = recommendation;
    }

    public String getPositiveFeedback() {
        return positiveFeedback;
    }

    public void setPositiveFeedback(String positiveFeedback) {
        this.positiveFeedback = positiveFeedback;
    }

    public String getImprovementFeedback() {
        return improvementFeedback;
    }

    public void setImprovementFeedback(String improvementFeedback) {
        this.improvementFeedback = improvementFeedback;
    }
}
