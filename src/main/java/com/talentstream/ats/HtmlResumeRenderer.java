package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.service.ResumeAIEnhancerService;

import org.springframework.stereotype.Component;

@Component
public class HtmlResumeRenderer {
	private final ResumeAIEnhancerService resumeAIEnhancerService ;
	
    public HtmlResumeRenderer(ResumeAIEnhancerService resumeAIEnhancerService) {
		super();
		this.resumeAIEnhancerService = resumeAIEnhancerService;
	}

	public String render(ResumeSchemaDTO resume) {
		 String rawSummary = extractSummary(resume);
		// Call AI (if summary exists)
		    String enhancedSummary = rawSummary != null
		            ? resumeAIEnhancerService.enhanceSummary(rawSummary)
		            : null;
	
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset=\"UTF-8\"/>");
        html.append("<title>ATS Resume</title>");
        html.append("</head><body>");

        // Header
        html.append("<h1>").append(resume.getHeader()).append("</h1>");
        html.append("<hr/>");

        // Sections
        if (resume.getSections() != null) {
            for (ResumeSchemaDTO.Section section : resume.getSections()) {

                if (section.getLines() == null || section.getLines().isEmpty()) {
                    continue; // skip empty sections
                }

                html.append("<h2>").append(section.getTitle()).append("</h2>");
                html.append("<ul>");

                for (String line : section.getLines()) {
                	 // If this is the SUMMARY section, replace with enhanced version
                    if (section.getTitle().equalsIgnoreCase("SUMMARY") && enhancedSummary != null) {
                        html.append("<li>").append(enhancedSummary).append("</li>");
                    } else {
                        html.append("<li>").append(line).append("</li>");
                    }
                    //html.append("<li>").append(line).append("</li>");
                }

                html.append("</ul>");
            }
        }

        html.append("</body></html>");
        return html.toString();
    }
	private String extractSummary(ResumeSchemaDTO resume) {
	    if (resume.getSections() == null) return null;

	    return resume.getSections().stream()
	            .filter(s -> "SUMMARY".equalsIgnoreCase(s.getTitle()))
	            .findFirst()
	            .flatMap(s -> s.getLines().stream().findFirst())
	            .orElse(null);
	}

}
