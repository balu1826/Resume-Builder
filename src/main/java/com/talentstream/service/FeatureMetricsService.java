package com.talentstream.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class FeatureMetricsService {

    private final MeterRegistry meterRegistry;

    public FeatureMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementFeature(String eventName) {
        Counter.builder("feature_usage_total")
                .tag("feature", eventName)
                .register(meterRegistry)
                .increment();
    }
    public void recordPageTime(String page, long durationMs) {
        Timer.builder("page_time_spent_seconds")
            .tag("page", page)
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }

}
