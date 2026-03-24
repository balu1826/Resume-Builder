package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.service.ResumeAIEnhancerService;

import org.springframework.stereotype.Component;

/**
 * HtmlResumeRendererV2 — "Emerald Executive" Template
 *
 * Design Identity:
 *  - Full-width header banner with a 5px emerald-green accent bar on top
 *  - Deep navy background in banner; Georgia serif for name, Arial for body
 *  - Section titles: uppercase Georgia, 3px left emerald border + light teal background tint
 *  - Skills: rounded deep-teal pill tags with pale mint text
 *  - Projects: card-style blocks with 4px left teal border and faint teal wash
 *  - Role shown as an inline emerald badge inside each project card
 *  - Education: teal diamond (◆) bullet per entry
 *  - Contact info: inline meta strip below name in banner
 *
 * Intentionally distinct from:
 *  V1 — Blue bordered section cards, periwinkle pill skills, Segoe UI
 *  V3 — Times New Roman, underlined headings, 4-column skill grid table
 *  V4 — Dark sidebar two-column layout, skill tags in dark boxes
 */
@Component
public class HtmlResumeRendererV2 implements ResumeHtmlRenderer {

    private final ResumeAIEnhancerService resumeAIEnhancerService;

    public HtmlResumeRendererV2(ResumeAIEnhancerService resumeAIEnhancerService) {
        super();
        this.resumeAIEnhancerService = resumeAIEnhancerService;
    }

    @Override
    public String render(ResumeSchemaDTO resume,
                         String summary,
                         String role,
                         String jd) {
    

    	  
    	        StringBuilder html = new StringBuilder();

    	        /* ===== DOCTYPE + HEAD ===== */
    	        html.append("<!DOCTYPE html>");
    	        html.append("<html><head>");
    	        html.append("<meta charset='UTF-8'/>");
    	        html.append("<title>Resume</title>");

    	        /* ===== EMERALD EXECUTIVE CSS ===== */
    	        html.append("<style>");

    	        // Page: A4, zero margin so banner bleeds edge-to-edge
    	        html.append("@page { size: A4; margin: 0; }");
    	        html.append("html, body { margin: 0; padding: 0; background: #fff; }");

    	        // Body: Arial for readability, dark ink
    	        html.append("body {" +
    	                "font-family: Arial, Helvetica, sans-serif;" +
    	                "font-size: 10.5pt;" +
    	                "line-height: 1.55;" +
    	                "color: #1a1a2e;" +
    	                "}");

    	        // ── HEADER BANNER ─────────────────────────────────────────────────────────
    	        html.append(".header-banner {" +
    	                "background: #0d1b2a;" +
    	                "border-top: 5px solid #059669;" +
    	                "padding: 22px 32px 18px 32px;" +
    	                "color: #fff;" +
    	                "}");

    	        html.append(".header-name {" +
    	                "font-family: Georgia, 'Times New Roman', serif;" +
    	                "font-size: 26pt;" +
    	                "font-weight: 700;" +
    	                "letter-spacing: 1.5px;" +
    	                "margin: 0 0 4px 0;" +
    	                "color: #ffffff;" +
    	                "}");

    	        html.append(".header-role {" +
    	                "font-size: 13pt;" +
    	                "font-weight: 400;" +
    	                "color: #6ee7b7;" +
    	                "margin: 0 0 10px 0;" +
    	                "letter-spacing: 0.5px;" +
    	                "}");

    	        // Inline contact meta strip below name
    	        html.append(".meta-strip {" +
    	                "font-size: 9.5pt;" +
    	                "color: #94a3b8;" +
    	                "margin-top: 4px;" +
    	                "}");

    	        html.append(".meta-sep {" +
    	                "color: #059669;" +
    	                "margin: 0 8px;" +
    	                "}");

    	        // ── CONTENT AREA ──────────────────────────────────────────────────────────
    	        html.append(".content {" +
    	                "padding: 20px 32px 24px 32px;" +
    	                "}");

    	        // ── SECTION HEADINGS ──────────────────────────────────────────────────────
    	        html.append(".sec-heading {" +
    	                "font-family: Georgia, 'Times New Roman', serif;" +
    	                "font-size: 9.5pt;" +
    	                "font-weight: 700;" +
    	                "text-transform: uppercase;" +
    	                "letter-spacing: 1.8px;" +
    	                "color: #064e3b;" +
    	                "border-left: 3px solid #059669;" +
    	                "background: #ecfdf5;" +
    	                "padding: 5px 10px;" +
    	                "margin: 18px 0 10px 0;" +
    	                "}");

    	        // ── PROFILE ───────────────────────────────────────────────────────────────
    	        html.append(".profile-text {" +
    	                "font-size: 10.2pt;" +
    	                "color: #374151;" +
    	                "line-height: 1.65;" +
    	                "margin: 0;" +
    	                "}");

    	        // ── SKILLS ────────────────────────────────────────────────────────────────
    	        html.append(".skills-wrap { margin-top: 6px; }");

    	        html.append(".skill-pill {" +
    	                "display: inline-block;" +
    	                "background: #334155;" +        // dark box
    	                "color: #e2e8f0;" +             // light text
    	                "font-size: 9pt;" +
    	                "padding: 6px 10px;" +
    	                "border-radius: 6px;" +         // small radius (not pill)
    	                "margin: 4px 6px 4px 0;" +
    	                "border: 1px solid #475569;" +  // subtle border
    	                "}" );

    	        // ── LANGUAGES ─────────────────────────────────────────────────────────────
    	        html.append(".lang-text {" +
    	        	    "display: inline-block;" +
    	        	    "background: #1e293b;" +          // slightly deeper dark (better contrast)
    	        	    "color: #f1f5f9;" +               // brighter readable text
    	        	    "font-size: 10pt;" +              // 🔥 increased for readability
    	        	    "padding: 5px 12px;" +            // balanced padding
    	        	    "border-radius: 4px;" +           // cleaner than 6px (resume look)
    	        	    "margin: 6px 8px 6px 0;" +        // 🔥 better spacing between chips
    	        	    "border: 1px solid #475569;" +
    	        	    "line-height: 1.4;" +             // prevents cramped text
    	        	    "white-space: nowrap;" +          // prevents breaking like 'Telugu'
    	        	"}");
    	        // ── PROJECT CARDS ─────────────────────────────────────────────────────────
    	        html.append(".proj-card {" +
    	                "border-left: 4px solid #059669;" +
    	                "background: #f8fffe;" +
    	                "padding: 10px 14px;" +
    	                "margin-bottom: 12px;" +
    	                "}");

    	        html.append(".proj-name {" +
    	                "font-family: Georgia, 'Times New Roman', serif;" +
    	                "font-size: 11pt;" +
    	                "font-weight: 700;" +
    	                "color: #064e3b;" +
    	                "}");

    	        html.append(".proj-role-badge {" +
    	        	    "display: inline-block;" +
    	        	    "background: #059669;" +
    	        	    "color: #fff;" +
    	        	    "font-size: 8pt;" +
    	        	    "font-weight: 600;" +
    	        	    "padding: 2px 8px;" +
    	        	    "border-radius: 4px;" +
    	        	    "margin-top: 4px;" +  
    	        	    "margin-left:4px;"+// ✅ ADD THIS
    	        	"}");

    	        html.append(".proj-tech {" +
    	                "font-size: 9.5pt;" +
    	                "color: #6b7280;" +
    	                "font-style: italic;" +
    	                "margin-top: 3px;" +
    	                "}");

    	        html.append(".proj-row {" +
    	                "font-size: 10pt;" +
    	                "color: #374151;" +
    	                "margin-top: 5px;" +
    	                "line-height: 1.55;" +
    	                "}");

    	        html.append(".proj-label {" +
    	                "font-weight: 700;" +
    	                "color: #065f46;" +
    	                "}");

    	        // ── EDUCATION ────────────────────────────────────────────────────
    	        html.append(".edu-entry {" +
    	                "display: table;" +
    	                "width: 100%;" +
    	                "margin-bottom: 8px;" +
    	                "}");

    	        html.append(".edu-bullet {" +
    	        	    "display: table-cell;" +
    	        	    "width: 12px;" +
    	        	    "color: #059669;" +
    	        	    "font-size: 12pt;" +     // slightly bigger for visibility
    	        	    "vertical-align: top;" +
    	        	    "padding-top: 1px;" +
    	        	    "}");
    	        html.append(".edu-text {" +
    	                "display: table-cell;" +
    	                "font-size: 10.2pt;" +
    	                "color: #1a1a2e;" +
    	                "line-height: 1.5;" +
    	                "}");

    	        html.append("</style>");
    	        html.append("</head><body>");

    	        /* ===== HEADER BANNER ===== */
    	        String[] headerParts = (resume != null && resume.getHeader() != null)
    	                ? resume.getHeader().split("\\|")
    	                : new String[]{""};

    	        String candidateName = headerParts.length > 0 ? esc(headerParts[0]) : "";
    	        String candidateRole = headerParts.length > 1 ? esc(headerParts[1]) : "";

    	        html.append("<div class='header-banner'>");
    	        html.append("<div class='header-name'>").append(candidateName).append("</div>");
    	        if (!candidateRole.isEmpty()) {
    	            html.append("<div class='header-role'>").append(candidateRole).append("</div>");
    	        }
    	        if (headerParts.length > 2) {
    	            html.append("<div class='meta-strip'>");
    	            boolean firstMeta = true;
    	            for (int i = 2; i < headerParts.length; i++) {
    	                String meta = esc(headerParts[i]);
    	                if (meta.isEmpty()) continue;
    	                if (!firstMeta) html.append("<span class='meta-sep'>|</span>");
    	                html.append(meta);
    	                firstMeta = false;
    	            }
    	            html.append("</div>");
    	        }
    	        html.append("</div>"); // end header-banner

    	        html.append("<div class='content'>");

    	        /* ===== PROFILE / SUMMARY ===== */
    	        ResumeSchemaDTO.Section summarySection = getSection(resume, "SUMMARY");
    	        if (summarySection != null && !summarySection.getLines().isEmpty()) {
    	            html.append("<div class='sec-heading'>Profile</div>");
    	            String base = esc(summarySection.getLines().get(0));
    	            String enhanced = resumeAIEnhancerService.enhanceSummary(base, role, jd);
    	            html.append("<p class='profile-text'>").append(enhanced).append("</p>");
    	        }

    	        /* ===== SKILLS ===== */
    	        ResumeSchemaDTO.Section skills = getSection(resume, "SKILLS");
    	        if (skills != null && !skills.getLines().isEmpty()) {
    	            html.append("<div class='sec-heading'>Skills</div>");
    	            html.append("<div class='skills-wrap'>");
    	            for (String s : skills.getLines()) {
    	                if (s == null || s.trim().isEmpty()) continue;
    	                html.append("<span class='skill-pill'>").append(esc(s)).append("</span>");
    	            }
    	            html.append("</div>");
    	        }

    	        /* ===== LANGUAGES ===== */
    	   
    	        ResumeSchemaDTO.Section langs = getSection(resume, "LANGUAGES", "KNOWN LANGUAGES");
    	        if (langs != null && !langs.getLines().isEmpty()) {
    	            html.append("<div class='sec-heading'>Languages</div>");
    	            html.append("<div>");

    	            boolean firstLang = true;
    	            for (String l : langs.getLines()) {
    	                if (l == null || l.trim().isEmpty()) continue;

    	                if (!firstLang) html.append("&#160;&#160;"); // ✅ removed # / symbols
    	                html.append("<span class='lang-text'>").append(esc(l)).append("</span>");

    	                firstLang = false;
    	            }

    	            html.append("</div>");
    	        }

    	        /* ===== PROJECTS ===== */
    	        /* ===== PROJECTS ===== */
    	        ResumeSchemaDTO.Section projects = getSection(resume, "PROJECTS");
    	        if (projects != null && projects.getLines() != null) {
    	            html.append("<div class='sec-heading'>Projects</div>");

    	            String projectName = "";
    	            String projectTech = "";
    	            String currentRole = "";
    	            StringBuilder projectContent = new StringBuilder();

    	            for (String line : projects.getLines()) {
    	                if (line == null || line.trim().isEmpty()) continue;

    	                if (line.contains("|")) {
    	                    // ✅ Render previous project BEFORE starting new one
    	                    if (!projectName.isEmpty()) {
    	                        html.append("<div class='proj-card'>");

    	                        html.append("<div class='proj-name'>")
    	                            .append(esc(projectName));

    	                        if (!currentRole.isEmpty()) {
    	                            html.append("<span class='proj-role-badge'>Role: ")
    	                                .append(esc(currentRole))
    	                                .append("</span>");
    	                        }

    	                        html.append("</div>");

    	                        if (!projectTech.isEmpty()) {
    	                            html.append("<div class='proj-tech'>")
    	                                .append(esc(projectTech))
    	                                .append("</div>");
    	                        }

    	                        html.append(projectContent.toString());
    	                        html.append("</div>");
    	                    }

    	                    // ✅ Reset for new project
    	                    String[] parts = line.split("\\|", 2);
    	                    projectName = parts[0].trim();
    	                    projectTech = parts.length > 1 ? parts[1].trim() : "";
    	                    currentRole = "";
    	                    projectContent.setLength(0);

    	                } else {
    	                    String cleanLine = line.replace("\u2022", "").trim();
    	                    String lower = cleanLine.toLowerCase();

    	                    if (lower.startsWith("role:")) {
    	                        currentRole = cleanLine.replaceFirst("(?i)role:", "").trim();

    	                    } else if (lower.startsWith("role description")) {
    	                        String val = cleanLine.replaceFirst("(?i)role description:", "").trim();
    	                        projectContent.append("<div class='proj-row'><span class='proj-label'>Role Description:</span> ")
    	                            .append(esc(val)).append("</div>");

    	                    } else if (lower.startsWith("description:")) {
    	                        String val = cleanLine.replaceFirst("(?i)description:", "").trim();
    	                        projectContent.append("<div class='proj-row'><span class='proj-label'>Description:</span> ")
    	                            .append(esc(val)).append("</div>");

    	                    } else {
    	                        projectContent.append("<div class='proj-row'><span class='proj-label'>Description:</span> ")
    	                            .append(esc(cleanLine)).append("</div>");
    	                    }
    	                }
    	            }

    	            // ✅ Render LAST project
    	            if (!projectName.isEmpty()) {
    	                html.append("<div class='proj-card'>");

    	                html.append("<div class='proj-name'>")
    	                    .append(esc(projectName));

    	                if (!currentRole.isEmpty()) {
    	                    html.append("<span class='proj-role-badge'>Role: ")
    	                        .append(esc(currentRole))
    	                        .append("</span>");
    	                }

    	                html.append("</div>");

    	                if (!projectTech.isEmpty()) {
    	                    html.append("<div class='proj-tech'>")
    	                        .append(esc(projectTech))
    	                        .append("</div>");
    	                }

    	                html.append(projectContent.toString());
    	                html.append("</div>");
    	            }
    	        }

    	        /* ===== EDUCATION ===== */
    	        ResumeSchemaDTO.Section edu = getSection(resume, "EDUCATION");
    	        if (edu != null && !edu.getLines().isEmpty()) {
    	            html.append("<div class='sec-heading'>Education</div>");
    	            for (String e : edu.getLines()) {
    	                if (e == null || e.trim().isEmpty()) continue;
    	                html.append("<div class='edu-entry'>")
    	                .append("<div class='edu-bullet'>•</div>")
    	                    .append("<div class='edu-text'>").append(esc(e)).append("</div>")
    	                    .append("</div>");
    	            }
    	        }
    	        html.append("</div>"); // end content
    	        html.append("</body></html>");

    	        return html.toString();
    	    }
    

    // ===== helpers =====

    private ResumeSchemaDTO.Section getSection(ResumeSchemaDTO resume, String... titles) {
        if (resume == null || resume.getSections() == null) return null;
        for (String t : titles) {
            for (ResumeSchemaDTO.Section s : resume.getSections()) {
                if (s != null && s.getTitle() != null && s.getTitle().equalsIgnoreCase(t))
                    return s;
            }
        }
        return null;
    }

    private String esc(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("-")) s = s.substring(1).trim();
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
