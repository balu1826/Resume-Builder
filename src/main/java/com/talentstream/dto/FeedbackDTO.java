package com.talentstream.dto;

import com.talentstream.entity.FeedbackField;
import com.talentstream.entity.FeedbackForm;

import java.util.ArrayList;
import java.util.List;

public class FeedbackDTO {

    private Long id;
    private String collegeName;
    private String mentorName;
    private String sessionTitle;
    private List<FeedbackFieldDTO> fields;

    public FeedbackDTO() {}

    public FeedbackDTO(Long id, String collegeName, String mentorName, String sessionTitle, List<FeedbackFieldDTO> fields) {
        this.id = id;
        this.collegeName = collegeName;
        this.mentorName = mentorName;
        this.sessionTitle = sessionTitle;
        this.fields = fields;
    }

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }

    public List<FeedbackFieldDTO> getFields() { return fields; }
    public void setFields(List<FeedbackFieldDTO> fields) { this.fields = fields; }

    // --- mapping helpers ---
    public static FeedbackDTO fromEntity(FeedbackForm form) {
        if (form == null) return null;

        List<FeedbackFieldDTO> fieldDTOs = new ArrayList<>();
        if (form.getFields() != null) {
            for (FeedbackField f : form.getFields()) {
                fieldDTOs.add(FeedbackFieldDTO.fromEntity(f));
            }
        }

        return new FeedbackDTO(
                form.getId(),
                form.getCollegeName(),
                form.getMentorName(),
                form.getSessionTitle(),
                fieldDTOs
        );
    }

    public FeedbackForm toEntity() {
        FeedbackForm form = new FeedbackForm();
        form.setId(this.id);
        form.setCollegeName(this.collegeName);
        form.setMentorName(this.mentorName);
        form.setSessionTitle(this.sessionTitle);

        List<FeedbackField> fieldEntities = new ArrayList<>();
        if (this.fields != null) {
            for (FeedbackFieldDTO dto : this.fields) {
                FeedbackField f = dto.toEntity();
                f.setForm(form); // back-reference
                fieldEntities.add(f);
            }
        }
        form.setFields(fieldEntities);
        return form;
    }
}
