package com.talentstream.entity;

import javax.persistence.*;

@Entity
@Table(name = "feedback_field")
public class FeedbackField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @Column(name = "field_type")
    private String fieldType; // text | textarea | rating | dropdown | number | checkbox

    @Column(name = "is_required")
    private boolean required;

    @Lob
    private String options; // JSON for dropdown etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private FeedbackForm form;

    public Long getId() { return id; }
    public String getLabel() { return label; }
    public String getFieldType() { return fieldType; }
    public boolean isRequired() { return required; }
    public String getOptions() { return options; }
    public FeedbackForm getForm() { return form; }

    public void setId(Long id) { this.id = id; }
    public void setLabel(String label) { this.label = label; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public void setRequired(boolean required) { this.required = required; }
    public void setOptions(String options) { this.options = options; }
    public void setForm(FeedbackForm form) { this.form = form; }
}
