package com.talentstream.dto;

import java.time.LocalDateTime;


public interface NotificationProjectionDTO {

    Long getId();
    LocalDateTime getCreatedTime();
    String getFeature();
    Long getFeatureId();
    String getMessage();
    String getSeenApplicantId();
}