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

    @Override
    public String render(ResumeSchemaDTO resume, String summary, String role, String jd) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset=\"UTF-8\"/>");
        html.append("<title>ATS Resume</title>");
        html.append("<style>");
        html.append("@page{size:A4;margin:12mm;}");
        html.append("html,body{margin:0;padding:0;background:#ffffff;height:100%;}");
        html.append("body{font-family:'Segoe UI',Tahoma,Arial,sans-serif;font-size:11pt;line-height:1.5;color:#0f172a;}");
        html.append(".page{width:186mm;margin:0 auto;background:#ffffff;border:1px solid #dbeafe;min-height:100vh;}");
        html.append(".top-strip{height:10px;background:#1d4ed8;}");
        html.append(".header{padding:12mm 10mm 7mm 10mm;background:#eff6ff;border-bottom:2px solid #bfdbfe;}");
        html.append(".name{font-size:24pt;font-weight:800;letter-spacing:.3px;margin:0;color:#0b1f66;}");
        html.append(".meta{margin-top:2mm;font-size:10.5pt;color:#334155;}");
        html.append(".meta-item{display:inline-block;margin-right:8px;}");
        html.append(".body-wrap{padding:8mm 10mm 10mm 10mm;border-left:1px solid #dbeafe;border-right:1px solid #dbeafe;}");
        html.append(".sec{margin-bottom:4.5mm;border:1px solid #e2e8f0;border-radius:8px;overflow:hidden;}");
        html.append(".sec h2{margin:0;padding:2.4mm 3.2mm;font-size:10.3pt;font-weight:700;text-transform:uppercase;letter-spacing:.6px;color:#1e3a8a;background:#eff6ff;border-bottom:1px solid #dbeafe;}");
        html.append(".sec-body{padding:2.8mm 3.2mm;}");
        html.append("ul{margin:0 0 0 5mm;padding:0;}");
        html.append("li{margin:0 0 1.5mm 0;}");
        html.append(".summary{font-size:10.8pt;color:#1e293b;}");
        html.append(".skills-row{margin:-1mm 0 0 -1mm;}");
        html.append(".skill-pill{display:inline-block;margin:1mm 0 0 1mm;padding:1.2mm 2.4mm;border:1px solid #c7d2fe;border-radius:999px;background:#f8faff;font-size:10pt;color:#1e3a8a;}");
        html.append(".lang-line{font-size:10.5pt;color:#334155;}");
        html.append(".proj{margin:0 0 3mm 0;padding:2.6mm;border:1px solid #dbeafe;border-radius:6px;background:#fcfdff;}");
        html.append(".proj:last-child{margin-bottom:0;}");
        html.append(".proj-title{font-weight:700;color:#0f172a;font-size:10.8pt;}");
        html.append(".proj-tech{font-size:10pt;color:#475569;margin-top:.8mm;}");
        html.append(".proj-row{font-size:10.2pt;color:#334155;margin-top:1.2mm;}");
        html.append(".label{font-weight:700;color:#0f172a;}");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='page'>");
        html.append("<div class='top-strip'></div>");

        String[] headerParts = safeSplit(resume != null ? resume.getHeader() : null);
        String name = headerParts.length > 0 ? headerParts[0] : "";

        html.append("<div class='header'>");
        html.append("<div class='name'>").append(esc(name)).append("</div>");
        if (headerParts.length > 1) {
            html.append("<div class='meta'>");
            for (int i = 1; i < headerParts.length; i++) {
                String value = esc(headerParts[i]);
                if (value.isEmpty()) {
                    continue;
                }
                html.append("<span class='meta-item'>").append(value).append("</span>");
            }
            html.append("</div>");
        }
        html.append("</div>");
        html.append("<div class='body-wrap'>");

        ResumeSchemaDTO.Section summarySection = getSection(resume, "SUMMARY");
        if (summarySection != null && hasVisibleLines(summarySection)) {
            String summaryBase = esc(summarySection.getLines().get(0));
            String enhancedSummary = resumeAIEnhancerService.enhanceSummary(summaryBase, role, jd);
            if (!enhancedSummary.isBlank()) {
            html.append("<div class='sec'>");
            html.append("<h2>Summary</h2>");
            html.append("<div class='sec-body'>");
            html.append("<div class='summary'>").append(esc(enhancedSummary)).append("</div>");
            html.append("</div>");
            html.append("</div>");
            }
        }

        if (resume != null && resume.getSections() != null) {
            for (ResumeSchemaDTO.Section section : resume.getSections()) {
                if (section == null || section.getTitle() == null || section.getLines() == null || section.getLines().isEmpty()) {
                    continue;
                }
                if ("SUMMARY".equalsIgnoreCase(section.getTitle())) {
                    continue;
                }

                String title = section.getTitle().trim();
                if ("PROJECTS".equalsIgnoreCase(title)) {
                    renderProjects(html, section);
                    continue;
                }

                if ("SKILLS".equalsIgnoreCase(title)) {
                    renderSkills(html, section);
                    continue;
                }

                if ("KNOWN LANGUAGES".equalsIgnoreCase(title) || "LANGUAGES".equalsIgnoreCase(title)) {
                    renderLanguages(html, section);
                    continue;
                }

                html.append("<div class='sec'>");
                html.append("<h2>").append(esc(title)).append("</h2>");
                html.append("<div class='sec-body'>");
                html.append("<ul>");
                for (String line : section.getLines()) {
                    if (line == null || line.trim().isEmpty()) {
                        continue;
                    }
                    html.append("<li>").append(esc(line)).append("</li>");
                }
                html.append("</ul>");
                html.append("</div>");
                html.append("</div>");
            }
        }

        html.append("</div>");
        html.append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    private void renderProjects(StringBuilder html, ResumeSchemaDTO.Section section) {
        if (!hasVisibleLines(section)) {
            return;
        }
        html.append("<div class='sec'>");
        html.append("<h2>Projects</h2>");
        html.append("<div class='sec-body'>");

        boolean projectOpen = false;
        for (String raw : section.getLines()) {
            if (raw == null) {
                continue;
            }
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.contains("|")) {
                if (projectOpen) {
                    html.append("</div>");
                }
                String[] parts = line.split("\\|", 2);
                html.append("<div class='proj'>");
                html.append("<div class='proj-title'>").append(esc(parts[0].trim())).append("</div>");
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    html.append("<div class='proj-tech'>").append(esc(parts[1].trim())).append("</div>");
                }
                projectOpen = true;
                continue;
            }

            if (!projectOpen) {
                continue;
            }

            String clean = line.replace("•", "").trim();
            String lower = clean.toLowerCase();
            if (lower.startsWith("role description")) {
                String value = clean.replaceFirst("(?i)role description:", "").trim();
                html.append("<div class='proj-row'><span class='label'>Role Description:</span> ")
                        .append(esc(value))
                        .append("</div>");
            } else if (lower.startsWith("role:")) {
                String value = clean.replaceFirst("(?i)role:", "").trim();
                html.append("<div class='proj-row'><span class='label'>Role:</span> ")
                        .append(esc(value))
                        .append("</div>");
            } else {
                html.append("<div class='proj-row'><span class='label'>Description:</span> ")
                        .append(esc(clean))
                        .append("</div>");
            }
        }
        if (projectOpen) {
            html.append("</div>");
        }
        html.append("</div>");
        html.append("</div>");
    }

    private void renderSkills(StringBuilder html, ResumeSchemaDTO.Section section) {
        if (!hasVisibleLines(section)) {
            return;
        }
        html.append("<div class='sec'>");
        html.append("<h2>Skills</h2>");
        html.append("<div class='sec-body'>");
        html.append("<div class='skills-row'>");
        for (String line : section.getLines()) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            html.append("<span class='skill-pill'>").append(esc(line)).append("</span>");
        }
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
    }

    private void renderLanguages(StringBuilder html, ResumeSchemaDTO.Section section) {
        if (!hasVisibleLines(section)) {
            return;
        }
        html.append("<div class='sec'>");
        html.append("<h2>Languages</h2>");
        html.append("<div class='sec-body'>");

        StringBuilder line = new StringBuilder();
        for (String value : section.getLines()) {
            String clean = esc(value);
            if (clean.isEmpty()) {
                continue;
            }
            if (line.length() > 0) {
                line.append(" | ");
            }
            line.append(clean);
        }
        html.append("<div class='lang-line'>").append(line).append("</div>");
        html.append("</div>");
        html.append("</div>");
    }

    private boolean hasVisibleLines(ResumeSchemaDTO.Section section) {
        if (section == null || section.getLines() == null) {
            return false;
        }
        for (String line : section.getLines()) {
            if (line != null && !line.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private ResumeSchemaDTO.Section getSection(ResumeSchemaDTO resume, String... titles) {
        if (resume == null || resume.getSections() == null) {
            return null;
        }
        for (String title : titles) {
            for (ResumeSchemaDTO.Section section : resume.getSections()) {
                if (section != null && section.getTitle() != null && section.getTitle().equalsIgnoreCase(title)) {
                    return section;
                }
            }
        }
        return null;
    }

    private String[] safeSplit(String header) {
        if (header == null) {
            return new String[0];
        }
        String[] parts = header.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i] == null ? "" : parts[i].trim();
        }
        return parts;
    }

    private String esc(String s) {
        if (s == null) {
            return "";
        }
        s = s.trim();
        if (s.startsWith("-")) {
            s = s.substring(1).trim();
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
