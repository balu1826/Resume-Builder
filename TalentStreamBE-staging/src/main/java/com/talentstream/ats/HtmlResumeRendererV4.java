package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.service.ResumeAIEnhancerService;
import org.springframework.stereotype.Component;

@Component
public class HtmlResumeRendererV4 implements ResumeHtmlRenderer {

    private final ResumeAIEnhancerService resumeAIEnhancerService;

    public HtmlResumeRendererV4(ResumeAIEnhancerService resumeAIEnhancerService) {
        this.resumeAIEnhancerService = resumeAIEnhancerService;
    }

    @Override
    public String render(ResumeSchemaDTO resume, String summary, String role, String jd) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset=\"UTF-8\"/>");
        html.append("<title>Resume</title>");
        html.append("<style>");
        html.append(
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f3f4f6; color: #333; }");

        // Use a table layout instead of flexbox for PDF compatibility
        html.append("table { width: 100%; border-collapse: collapse; table-layout: fixed; }");
        html.append("td { vertical-align: top; }");

        // Sidebar styles (left column)
        html.append(".left-col { width: 30%; background-color: #1e293b; color: #fff; padding: 30px 20px; }");
        html.append(
                ".left-col h2 { color: #94a3b8; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; border-bottom: 1px solid #334155; padding-bottom: 5px; margin-top: 30px; margin-bottom: 15px; }");
        html.append(".left-col .contact-info div { margin-bottom: 10px; font-size: 13px; word-break: break-all; }");
        html.append(
                ".left-col .skill-tag { display: inline-block; background-color: #334155; color: #e2e8f0; padding: 4px 8px; border-radius: 4px; font-size: 12px; margin: 0 5px 5px 0; }");
        html.append(".left-col ul { list-style-type: none; padding: 0; margin: 0; }");
        html.append(".left-col li { margin-bottom: 8px; font-size: 13px; line-height: 1.4; color: #cbd5e1; }");

        // Main content styles (right column)
        html.append(".right-col { width: 70%; background-color: #ffffff; padding: 40px 30px; }");
        html.append(".main-header { margin-bottom: 30px; border-bottom: 2px solid #e2e8f0; padding-bottom: 20px; }");
        html.append(
                "h1 { margin: 0; font-size: 32px; color: #0f172a; text-transform: uppercase; letter-spacing: 1px; }");
        html.append(".role-title { color: #2563eb; font-size: 18px; font-weight: 500; margin-top: 5px; }");

        html.append(".section { margin-bottom: 25px; }");
        html.append(
                ".section-title { font-size: 16px; font-weight: 700; color: #0f172a; text-transform: uppercase; border-left: 4px solid #2563eb; padding-left: 10px; margin-bottom: 15px; }");
        html.append(".summary-text { font-size: 14px; line-height: 1.6; color: #475569; }");

        html.append(".project-item { margin-bottom: 20px; }");
        html.append(
                ".project-header { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 5px; }");
        // Note: Ideally flex shouldn't be used inside project-header either if it fails
        // inside tables, but let's see.
        // If the project header flex fails, we can use a nested table or spans with
        // floats.
        // Let's use a nested table for the project header to be safe.

        html.append(".project-head-table { width: 100%; margin-bottom: 5px; }");
        html.append(".project-head-table td { padding: 0; vertical-align: bottom; }");
        html.append(".project-name { font-weight: 700; font-size: 15px; color: #1e293b; }");
        html.append(".project-tech { font-size: 12px; color: #64748b; font-style: italic; text-align: right; }");

        html.append(".project-desc ul { margin: 5px 0 0 18px; padding: 0; }");
        html.append(".project-desc li { font-size: 13px; margin-bottom: 4px; color: #475569; line-height: 1.5; }");

        html.append(".edu-item { margin-bottom: 15px; }");
        html.append(".edu-degree { font-weight: 600; font-size: 14px; color: #1e293b; }");
        html.append(".edu-school { font-size: 13px; color: #475569; }");
        html.append(".edu-meta { font-size: 12px; color: #64748b; }");
        html.append(
        		"@page { size: A4; margin: 0; }");

        		html.append(
        		"html, body { margin: 0; padding: 0; height: 100%; background: #ffffff; }");

        		html.append(
        		"body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #333; }");

        		/* Main table full A4 height */
        		html.append(
        		"table.main-table { width: 210mm; height: 297mm; border-collapse: collapse; table-layout: fixed; }");

        		/* Remove gaps */
        		html.append("td { vertical-align: top; padding: 0; }");

        		/* LEFT COLUMN FULL DARK */
        		html.append(
        		".left-col { width: 30%; background-color: #1e293b; color: #fff; padding: 30px 20px; height: 297mm; }");

        		/* RIGHT COLUMN FULL WHITE */
        		html.append(
        		".right-col { width: 70%; background-color: #ffffff; padding: 40px 30px; height: 297mm; }");
        html.append("</style></head><body>");

        // Main Table Container
        html.append("<table class=\"main-table\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">");
        html.append("<tr>");

        // Prepare Header Data
        String[] headerParts = resume.getHeader().split("\\|");
        String name = headerParts.length > 0 ? headerParts[0].trim() : "";
        String roleTitle = headerParts.length > 1 ? headerParts[1].trim() : "";

        // --- LEFT COLUMN (SIDEBAR) ---
        html.append("<td class=\"left-col\">");

        // Contact Info
        html.append("<div class=\"contact-info\">");
        html.append("<h2>Contact</h2>");
        for (int i = 2; i < headerParts.length; i++) {
            html.append("<div>").append(esc(headerParts[i])).append("</div>");
        }
        html.append("</div>");

        // Skills
        ResumeSchemaDTO.Section skills = getSection(resume, "SKILLS");
        if (skills != null && !skills.getLines().isEmpty()) {
            html.append("<h2>Skills</h2>");
            html.append("<div>");
            for (String s : skills.getLines()) {
                html.append("<span class=\"skill-tag\">").append(esc(s)).append("</span>");
            }
            html.append("</div>");
        }

        // Languages
        ResumeSchemaDTO.Section langs = getSection(resume, "LANGUAGES", "KNOWN LANGUAGES");
        if (langs != null && !langs.getLines().isEmpty()) {
            html.append("<h2>Languages</h2>");
            html.append("<ul>");
            for (String l : langs.getLines()) {
                html.append("<li>").append(esc(l)).append("</li>");
            }
            html.append("</ul>");
        }
        html.append("</td>"); // End Left Column

        // --- RIGHT COLUMN (MAIN CONTENT) ---
        html.append("<td class=\"right-col\">");

        // Header
        html.append("<div class=\"main-header\">");
        html.append("<h1>").append(esc(name)).append("</h1>");
        if (!roleTitle.isEmpty()) {
            html.append("<div class=\"role-title\">").append(esc(roleTitle)).append("</div>");
        }
        html.append("</div>");

        // Profile / Summary
        ResumeSchemaDTO.Section summarySection = getSection(resume, "SUMMARY");
        if (summarySection != null && !summarySection.getLines().isEmpty()) {
            html.append("<div class=\"section\">");
            html.append("<div class=\"section-title\">Profile</div>");
            html.append("<div class=\"summary-text\">")
                    .append(resumeAIEnhancerService.enhanceSummary(esc(summarySection.getLines().get(0)), role, jd))
                    .append("</div>");
            html.append("</div>");
        }

        // Projects
        ResumeSchemaDTO.Section projects = getSection(resume, "PROJECTS");
        if (projects != null && projects.getLines() != null) {
            html.append("<div class=\"section\">");
            html.append("<div class=\"section-title\">Experience</div>");

            boolean projectOpen = false;
            for (String line : projects.getLines()) {
                if (line == null || line.trim().isEmpty())
                    continue;

                if (line.contains("|")) {
                    if (projectOpen) {
                        html.append("</ul></div></div>");
                    }
                    String[] parts = line.split("\\|", 2);
                    html.append("<div class=\"project-item\">");

                    // Use nested table for project header
                    html.append("<table class=\"project-head-table\"><tr>");
                    html.append("<td><span class=\"project-name\">").append(esc(parts[0].trim()))
                            .append("</span></td>");
                    if (parts.length > 1) {
                        html.append("<td align=\"right\"><span class=\"project-tech\">").append(esc(parts[1].trim()))
                                .append("</span></td>");
                    }
                    html.append("</tr></table>");

                    html.append("<div class=\"project-desc\"><ul>");
                    projectOpen = true;
                } else if (projectOpen) {
                    html.append("<li>").append(esc(line)).append("</li>");
                }
            }
            if (projectOpen) {
                html.append("</ul></div></div>");
            }
            html.append("</div>");
        }

        // Education
        ResumeSchemaDTO.Section edu = getSection(resume, "EDUCATION");
        if (edu != null && !edu.getLines().isEmpty()) {
            html.append("<div class=\"section\">");
            html.append("<div class=\"section-title\">Education</div>");
            for (String e : edu.getLines()) {
                html.append("<div class=\"edu-item\">");
                html.append("<div class=\"edu-degree\">").append(esc(e)).append("</div>");
                html.append("</div>");
            }
            html.append("</div>");
        }

        html.append("</td>"); // End Right Column
        html.append("</tr></table>"); // End Main Table

        html.append("</body></html>");

        return html.toString();
    }

    private ResumeSchemaDTO.Section getSection(ResumeSchemaDTO resume, String... titles) {
        for (String t : titles) {
            for (ResumeSchemaDTO.Section s : resume.getSections()) {
                if (s.getTitle().equalsIgnoreCase(t))
                    return s;
            }
        }
        return null;
    }

    private String esc(String s) {
        if (s == null)
            return "";
        s = s.trim();
        if (s.startsWith("-"))
            s = s.substring(1).trim();
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
