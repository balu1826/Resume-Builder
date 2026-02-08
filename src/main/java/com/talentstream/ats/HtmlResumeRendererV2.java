package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;
import org.springframework.stereotype.Component;

@Component
public class HtmlResumeRendererV2 implements ResumeHtmlRenderer {

    @Override
    public String render(ResumeSchemaDTO resume,
                         String summary,
                         String role,
                         String jd) {
    	StringBuilder html = new StringBuilder();

    	html.append("<!DOCTYPE html>");
    	html.append("<html>");
    	html.append("<head>");
    	html.append("<meta charset='UTF-8'/>");
    	html.append("<title>Resume</title>");

    	/* ===== INLINE CSS ===== */
    	html.append("<style>");
    	html.append("body{font-family:Arial,Helvetica,sans-serif;margin:0;background:#f8fafc;color:#0f172a;}");
    	html.append(".page{max-width:900px;margin:0 auto;background:#fff;}");
    	html.append(".header{padding:24px;border-bottom:1px solid #e5e7eb;}");
    	html.append(".name{font-size:28px;font-weight:800;}");
    	html.append(".meta{font-size:13px;color:#475569;margin-bottom:4px;}");
    	html.append(".container{display:flex;padding:24px;gap:24px;}");
    	html.append(".left{width:33%;}");
    	html.append(".right{width:67%;border-left:1px solid #e5e7eb;padding-left:24px;}");
    	html.append("h2{font-size:12px;text-transform:uppercase;letter-spacing:1px;color:#94a3b8;margin-bottom:8px;}");
    	html.append("p{font-size:13px;line-height:1.5;margin:4px 0;}");
    	html.append("ul{padding-left:16px;margin:6px 0;}");
    	html.append("li{font-size:12px;margin-bottom:6px;}");
    	html.append(".skill{display:inline-block;background:#e0e7ff;color:#2563eb;font-size:10px;font-weight:700;padding:4px 8px;border-radius:6px;margin:3px;}");
    	html.append(".project{margin-bottom:16px;}");
    	html.append(".project-title{font-size:14px;font-weight:700;}");
    	
    	html.append("</style>");

    	html.append("</head>");
    	html.append("<body>");
    	html.append("<div class='page'>");

    	/* ===== HEADER (from ResumeSchemaDTO) ===== */
    	html.append("<div class='header'>");

    	String[] headerParts = resume.getHeader().split("\\|");
    	html.append("<div class='name'>").append(esc(headerParts[0])).append("</div>");
    	for (int i = 1; i < headerParts.length; i++) {
    	    html.append("<div class='meta'>").append(esc(headerParts[i])).append("</div>");
    	}

    	html.append("</div>");

    	/* ===== BODY ===== */
    	html.append("<div class='container'>");

    	/* ===== LEFT COLUMN ===== */
    	html.append("<div class='left'>");

    	/* PROFILE (SUMMARY section) */
    	ResumeSchemaDTO.Section summarySection = getSection(resume, "SUMMARY");
    	if (summarySection != null && !summarySection.getLines().isEmpty()) {
    	    html.append("<h2>Profile</h2>");
    	    html.append("<p>").append(esc(summarySection.getLines().get(0))).append("</p>");
    	}

    	/* SKILLS */
    	ResumeSchemaDTO.Section skills = getSection(resume, "SKILLS");
    	if (skills != null) {
    	    html.append("<h2>Skills</h2>");
    	    html.append("<div>");
    	    for (String s : skills.getLines()) {
    	        html.append("<span class='skill'>").append(esc(s)).append("</span>");
    	    }
    	    html.append("</div>");
    	}

    	/* LANGUAGES */
    	ResumeSchemaDTO.Section langs = getSection(resume, "LANGUAGES", "KNOWN LANGUAGES");
    	if (langs != null) {
    	    html.append("<h2>Languages</h2>");
    	    for (String l : langs.getLines()) {
    	        html.append("<p>").append(esc(l)).append("</p>");
    	    }
    	}

    	html.append("</div>");

    	/* ===== RIGHT COLUMN ===== */
    	html.append("<div class='right'>");

    	/* PROJECTS */
    	ResumeSchemaDTO.Section projects = getSection(resume, "PROJECTS");
    	if (projects != null) {
    	    html.append("<h2>Projects</h2>");

    	    boolean open = false;
    	    for (String line : projects.getLines()) {
    	        if (line.contains("|")) {
    	            if (open) html.append("</div>");
    	            html.append("<div class='project'>");
    	            html.append("<div class='project-title'>").append(esc(line)).append("</div>");
    	            open = true;
    	        } else {
    	            html.append("<p>").append(esc(line)).append("</p>");
    	        }
    	    }
    	    if (open) html.append("</div>");
    	}

    	/* EDUCATION */
    	ResumeSchemaDTO.Section edu = getSection(resume, "EDUCATION");
    	if (edu != null) {
    	    html.append("<h2>Education</h2>");
    	    for (String e : edu.getLines()) {
    	        html.append("<p>").append(esc(e)).append("</p>");
    	    }
    	}

    	html.append("</div>"); // right
    	html.append("</div>"); // container
    	html.append("</div>"); // page
    	html.append("</body></html>");

        return html.toString();
    }

    // ===== helpers =====

    private void addBulletSection(StringBuilder html,
                                  ResumeSchemaDTO resume,
                                  String... titles) {

        ResumeSchemaDTO.Section section = getSection(resume, titles);
        if (section == null || section.getLines().isEmpty()) return;

        html.append("<h2>").append(titles[0]).append("</h2><ul>");
        for (String line : section.getLines()) {
            html.append("<li>").append(esc(line)).append("</li>");
        }
        html.append("</ul>");
    }

    private ResumeSchemaDTO.Section getSection(ResumeSchemaDTO resume, String... titles) {
        for (String t : titles) {
            for (ResumeSchemaDTO.Section s : resume.getSections()) {
                if (s.getTitle().equalsIgnoreCase(t)) return s;
            }
        }
        return null;
    }

    private String esc(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("-")) s = s.substring(1).trim();
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&apos;");
    }
}
