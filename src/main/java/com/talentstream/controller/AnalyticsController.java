package com.talentstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.AnalyticsEventRequest;
import com.talentstream.service.FeatureMetricsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final FeatureMetricsService featureMetricsService;

    public AnalyticsController(FeatureMetricsService featureMetricsService) {
        this.featureMetricsService = featureMetricsService;
    }

    @PostMapping("/event")
    public ResponseEntity<Void> trackEvent(@RequestBody AnalyticsEventRequest request) {
        featureMetricsService.incrementFeature(request.getEventName());
        return ResponseEntity.ok().build();
    }
}
