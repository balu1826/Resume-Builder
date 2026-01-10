package com.talentstream.dto;

import java.util.List;

public class FeedbackFormDTO {
    private String collegeName;
    private String mentorName;
    private String sessionTitle;
    private List<FeedbackFieldDTO> fields;

    public String getCollegeName() { return collegeName; }
    public String getMentorName() { return mentorName; }
    public String getSessionTitle() { return sessionTitle; }
    public List<FeedbackFieldDTO> getFields() { return fields; }

    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
    public void setFields(List<FeedbackFieldDTO> fields) { this.fields = fields; }
}

