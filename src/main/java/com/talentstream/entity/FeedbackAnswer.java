package com.talentstream.entity;

import javax.persistence.*;

@Entity
@Table(name = "feedback_answer")
public class FeedbackAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @Lob
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id")
    private FeedbackResponse response;

    public Long getId() { return id; }
    public String getLabel() { return label; }
    public String getValue() { return value; }
    public FeedbackResponse getResponse() { return response; }

    public void setId(Long id) { this.id = id; }
    public void setLabel(String label) { this.label = label; }
    public void setValue(String value) { this.value = value; }
    public void setResponse(FeedbackResponse response) { this.response = response; }
}
