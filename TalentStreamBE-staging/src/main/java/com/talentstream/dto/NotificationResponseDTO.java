package com.talentstream.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NotificationResponseDTO {

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String feature;
    private Long featureId;
    private String message;
    private boolean seenStatus;

  

    public NotificationResponseDTO(Long id, LocalDateTime createdTime, String feature, Long featureId, String message,
			 boolean seenStatus) {
		super();
		this.id = id;
		this.createdTime = createdTime;
		this.feature = feature;
		this.featureId = featureId;
		this.message = message;
		this.seenStatus = seenStatus;
	}

	// Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public boolean getSeenStatus() {
		return seenStatus;
	}

	public void setSeenStatus(boolean seenStatus) {
		this.seenStatus = seenStatus;
	}
    
    
}