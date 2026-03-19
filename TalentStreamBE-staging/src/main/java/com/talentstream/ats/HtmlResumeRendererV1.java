package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.service.ResumeAIEnhancerService;

import org.springframework.stereotype.Component;

@Component
public class HtmlResumeRendererV1 implements ResumeHtmlRenderer {
    private final ResumeAIEnhancerService resumeAIEnhancerService;

    public HtmlResumeRendererV1(ResumeAIEnhancerService resumeAIEnhancerService) {
        super();
        this.resumeAIEnhancerService = resumeAIEnhancerService;
    }

    public String render(ResumeSchemaDTO resume, String summary, String role, String jd) {

        String enhancedSummary = resumeAIEnhancerService.enhanceSummary(summary, role, jd);

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset=\"UTF-8\"/>");
        html.append("<title>ATS Resume</title>");
        html.append("<style>");
        html.append(".project{margin-bottom:16px;}");
        html.append(
                ".project-title{font-family:Arial,sans-serif;font-size:14px;font-weight:700;display:flex;justify-content:space-between;gap:8px;}");
        html.append(".project-tech{font-size:11px;color:#64748b;white-space:nowrap;}");
        html.append(".project ul{padding-left:0;margin-top:6px;}");
        html.append(
                ".project li{font-family:Arial,sans-serif;list-style:none;font-size:13px;display:flex;align-items:flex-start;margin-bottom:6px;}");
        html.append(".dot{color:#2563eb;margin-right:8px;}");
        html.append("</style>");
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

                if ("PROJECTS".equalsIgnoreCase(section.getTitle())) {
                    html.append("<h2>").append(section.getTitle()).append("</h2>");
                    boolean projectOpen = false;
                    for (String line : section.getLines()) {
                        if (line == null || line.trim().isEmpty())
                            continue;
                        if (line.contains("|")) {
                            if (projectOpen)
                                html.append("</ul></div>");
                            String[] parts = line.split("\\|", 2);
                            html.append("<div class='project'>");
                            html.append("<div class='project-title'>");
                            html.append("<span>").append(parts[0].trim()).append("</span>");
                            if (parts.length > 1) {
                                html.append("<span class='project-tech'>").append(parts[1].trim()).append("</span>");
                            }
                            html.append("</div>");
                            html.append("<ul>");
                            projectOpen = true;
                        } else if (projectOpen) {
                            html.append("<li><span class='dot'>•</span>").append(line).append("</li>");
                        }
                    }
                    if (projectOpen)
                        html.append("</ul></div>");
                } else {
                    html.append("<h2>").append(section.getTitle()).append("</h2>");
                    html.append("<ul>");

                    for (String line : section.getLines()) {
                        // If this is the SUMMARY section, replace with enhanced version
                        if (section.getTitle().equalsIgnoreCase("SUMMARY") && enhancedSummary != null) {
                            html.append("<li>").append(enhancedSummary).append("</li>");
                        } else {
                            html.append("<li>").append(line).append("</li>");
                        }
                        // html.append("<li>").append(line).append("</li>");
                    }

                    html.append("</ul>");
                }
            }
        }

        html.append("</body></html>");
        return html.toString();
    }

}
