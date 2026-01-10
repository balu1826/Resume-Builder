package com.talentstream.dto;

import java.time.Instant;
import java.util.Map;

public class FeedbackResponseDTO {
    private Long id;
    private Long formId;
    private Instant createdAt;
    private Map<String, String> answers;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
}
