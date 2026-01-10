package com.talentstream.dto;
public class AnalyticsEventRequest {
    private String eventName;
    private String page;
    private Long durationMs;

    public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public Long getDurationMs() {
		return durationMs;
	}

	public void setDurationMs(Long durationMs) {
		this.durationMs = durationMs;
	}

	public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
