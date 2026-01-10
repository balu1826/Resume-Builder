package com.talentstream.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feedback_response")
public class FeedbackResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private FeedbackForm form;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackAnswer> answers = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }

    public FeedbackForm getForm() { return form; }
    public List<FeedbackAnswer> getAnswers() { return answers; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setForm(FeedbackForm form) { this.form = form; }
    public void setAnswers(List<FeedbackAnswer> answers) { this.answers = answers; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
