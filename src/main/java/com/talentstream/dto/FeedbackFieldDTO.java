package com.talentstream.dto;

import com.talentstream.entity.FeedbackField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

public class FeedbackFieldDTO {

    private Long id;
    private String label;
    private String fieldType;   // text | textarea | dropdown | number | rating | checkbox
    private boolean required;
    private String options;     // comma-separated or JSON string

    public FeedbackFieldDTO() {}

    public FeedbackFieldDTO(Long id, String label, String fieldType, boolean required, String options) {
        this.id = id;
        this.label = label;
        this.fieldType = fieldType;
        this.required = required;
        this.options = options;
    }

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    // --- mapping helpers ---
    public static FeedbackFieldDTO fromEntity(FeedbackField field) {
        if (field == null) return null;
        return new FeedbackFieldDTO(
                field.getId(),
                field.getLabel(),
                field.getFieldType(),
                field.isRequired(),
                field.getOptions()
        );
    }

    public FeedbackField toEntity() {
        FeedbackField f = new FeedbackField();
        f.setId(this.id);
        f.setLabel(this.label);
        f.setFieldType(this.fieldType);
        f.setRequired(this.required);
        f.setOptions(this.options);
        return f;
    }
    // --- Canonical getter/setter used by service ---
    @JsonProperty("required")
    public Boolean getRequired() { return required; }
    
    @JsonAlias({"required", "isRequired"})
    public void setRequired(Boolean required) { this.required = required; }

}
