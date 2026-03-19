package com.talentstream.entity;

import java.util.List;

public enum RatingCategory {

	TEACHING_EFFECTIVENESS(List.of("conceptClarity", "realWorldExamples", "sessionStructure")),
	SKILL_READINESS(List.of("handsOn", "industryRelevance", "confidence")),
	MENTOR_INTERACTION(List.of("interaction", "doubtHandling")),
	DELIVERY_DISCIPLINE(List.of("timeliness", "communication")),
	OVERALL_PROGRAM_HEALTH(List.of("overallValue", "recommendation"));

	private final List<String> fields;

	RatingCategory(List<String> fields) {
		this.fields = fields;
	}

	public List<String> getFields() {
		return fields;
	}
}
