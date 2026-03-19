package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.service.ResumeAIEnhancerService;

import org.springframework.stereotype.Component;

@Component
public class HtmlResumeRendererV2 implements ResumeHtmlRenderer {
	private final  ResumeAIEnhancerService  resumeAIEnhancerService ;
	
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

    	html.append("<!DOCTYPE html>");
    	html.append("<html>");
    	html.append("<head>");
    	html.append("<meta charset='UTF-8'/>");
    	html.append("<title>Resume</title>");

    	/* ===== INLINE CSS (PDF SAFE) ===== */
    	html.append("<style>");
    	html.append(
			    "@page { size: A4; margin: 0; }");

			html.append(
			    ".wrapper{" +
			    "width:210mm;" +
			    "height:297mm;" +          // fixed full A4 height
			    "margin:0 auto;" +
			    "padding:15mm;" +          // slightly reduced padding to avoid overflow
			    "box-sizing:border-box;" +
			    "overflow:hidden;" +       // prevents accidental second page
			    "}");
    	html.append(".page{max-width:640px;margin:0 auto;padding:24px;background:#fff;}");
    	html.append(
				"body {" +
				"font-family: Calibri, Arial, Helvetica, sans-serif;" +
				"font-size:20px;" +
				"color:#1f2937;" +
				"margin:0;" +
				"padding:0;" +
				"}");
    	html.append(".role{color:#2563eb;font-weight:600;font-size:14px;margin-bottom:8px;}");
    	html.append(".meta{font-size:12px;color:#64748b;margin-bottom:4px;}");
    	html.append("hr{border:none;border-top:1px solid #e5e7eb;margin:16px 0;}");
    	html.append("h2{font-size:11px;text-transform:uppercase;letter-spacing:1px;color:#94a3b8;margin:16px 0 8px;}");
    	html.append("p{font-size:17px;line-height:1.6;margin:4px 0;color:#334155;}");
    	html.append(".skills span{display:inline-block;background:#f1f5f9;padding:6px 10px;font-size:12px;border-radius:6px;margin:4px 4px 0 0;}");
    	html.append(".project{margin-bottom:16px;}");
    	html.append(".project-title{font-size:14px;font-weight:700;display:flex;justify-content:space-between;gap:8px;}");
    	html.append(".project-tech{font-size:11px;color:#64748b;white-space:nowrap;}");
    	html.append(".project ul{padding-left:0;margin-top:6px;}");
    	html.append(".project li{list-style:none;font-size:15px;display:flex;align-items:flex-start;margin-bottom:6px;}");
    	html.append(".dot{color:#2563eb;margin-right:8px;}");
    	html.append(".edu{border-left:1px solid #e5e7eb;padding-left:12px;margin-bottom:12px;}");
    	html.append("</style>");

    	html.append("</head>");
    	html.append("<body>");
    	html.append("<div class='page'>");

    	/* ===== HEADER ===== */
    	String[] headerParts = resume.getHeader().split("\\|");
    	html.append("<h1>").append(esc(headerParts[0])).append("</h1>");

    	if (headerParts.length > 1) {
    	    html.append("<div class='role'>").append(esc(headerParts[1])).append("</div>");
    	}
    	for (int i = 2; i < headerParts.length; i++) {
    	    html.append("<div class='meta'>").append(esc(headerParts[i])).append("</div>");
    	}

    	html.append("<hr/>");

    	/* ===== PROFILE ===== */
    	ResumeSchemaDTO.Section summarySection = getSection(resume, "SUMMARY");
    	if (summarySection != null && !summarySection.getLines().isEmpty()) {
    	    html.append("<h2>Profile</h2>");
    	    html.append("<p>")
    	        .append( resumeAIEnhancerService.enhanceSummary(esc(summarySection.getLines().get(0)), role, jd) )
    	        .append("</p>");
    	}

    	/* ===== SKILLS ===== */
    	ResumeSchemaDTO.Section skills = getSection(resume, "SKILLS");
    	if (skills != null && !skills.getLines().isEmpty()) {
    	    html.append("<h2>Skills</h2>");
    	    html.append("<div class='skills'>");
    	    for (String s : skills.getLines()) {
    	        html.append("<span>").append(esc(s)).append("</span>");
    	    }
    	    html.append("</div>");
    	}

    	/* ===== LANGUAGES ===== */
    	ResumeSchemaDTO.Section langs = getSection(resume, "LANGUAGES", "KNOWN LANGUAGES");
    	if (langs != null && !langs.getLines().isEmpty()) {
    	    html.append("<h2>Languages</h2>");
    	    html.append("<p>").append(esc(String.join(" • ", langs.getLines()))).append("</p>");
    	}

    	/* ===== PROJECTS ===== */
    	ResumeSchemaDTO.Section projects = getSection(resume, "PROJECTS");
    	if (projects != null && projects.getLines() != null) {

    	    html.append("<h2>Projects</h2>");

    	    boolean projectOpen = false;

    	    for (String line : projects.getLines()) {

    	        if (line == null || line.trim().isEmpty()) continue;

    	        if (line.contains("|")) {

    	            if (projectOpen) {
    	                html.append("</ul></div>");
    	            }

    	            String[] parts = line.split("\\|", 2);

    	            html.append("<div class='project'>");
    	            html.append("<div class='project-title'>");
    	            html.append("<span>").append(esc(parts[0].trim())).append("</span>");

    	            if (parts.length > 1) {
    	                html.append("<span class='project-tech'>")
    	                    .append(esc(parts[1].trim()))
    	                    .append("</span>");
    	            }

    	            html.append("</div>");
    	            html.append("<ul>");
    	            projectOpen = true;

    	        } else if (projectOpen) {
    	            html.append("<li><span class='dot'>•</span>")
    	                .append(esc(line))
    	                .append("</li>");
    	        }
    	    }

    	    if (projectOpen) {
    	        html.append("</ul></div>");
    	    }
    	}

    	/* ===== EDUCATION ===== */
    	ResumeSchemaDTO.Section edu = getSection(resume, "EDUCATION");
    	if (edu != null && !edu.getLines().isEmpty()) {
    	    html.append("<h2>Education</h2>");
    	    for (String e : edu.getLines()) {
    	        html.append("<div class='edu'><p>")
    	            .append(esc(e))
    	            .append("</p></div>");
    	    }
    	}

    	html.append("</div>");
    	html.append("</body>");
    	html.append("</html>");

    	return html.toString();

    }

    // ===== helpers =====



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
