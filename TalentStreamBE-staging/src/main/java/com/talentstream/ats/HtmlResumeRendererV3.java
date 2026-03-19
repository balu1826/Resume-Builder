package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.service.ResumeAIEnhancerService;

import org.springframework.stereotype.Component;

@Component
public class HtmlResumeRendererV3 implements ResumeHtmlRenderer {
	private final ResumeAIEnhancerService resumeAIEnhancerService;

	public HtmlResumeRendererV3(ResumeAIEnhancerService resumeAIEnhancerService) {
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
		html.append("<html><head>");
		html.append("<meta charset='UTF-8'/>");
		html.append("<title>Resume</title>");

		// Template V3: minimal + underlined headings + slightly more whitespace (ATS-safe)
		html.append("<style>");
		html.append("@page{size:A4;margin:18mm;}");
		html.append("html,body{margin:0;padding:0;background:#fff;}");
		html.append("body{font-family:Times New Roman,Times,serif;font-size:11pt;line-height:1.4;color:#111827;}");
		html.append(".page{max-width:180mm;margin:0 auto;}");
		html.append(".header{margin:0 0 5mm 0;}");
		html.append(".name{font-size:20pt;font-weight:700;margin:0;}");
		html.append(".meta{font-size:10.5pt;color:#374151;margin:1.5mm 0 0 0;}");
		html.append(".sec{margin:0 0 4mm 0;}");
		html.append(".sec h2{font-size:10.5pt;font-weight:700;text-transform:uppercase;margin:0 0 2mm 0;border-bottom:1px solid #111827;padding-bottom:1mm;}");
		html.append("ul{margin:0 0 0 5mm;padding:0;}");
		html.append("li{margin:0 0 1.4mm 0;}");
		html.append(".proj{margin:0 0 3.5mm 0;}");
		html.append(".proj-title{font-weight:700;}");
		html.append(".proj-tech{font-size:10pt;color:#374151;}");
		html.append("</style>");

		html.append("</head><body>");
		html.append("<div class='page'>");

		String[] headerParts = safeSplit(resume != null ? resume.getHeader() : null);
		String name = headerParts.length > 0 ? headerParts[0] : "";
		html.append("<div class='header'>");
		html.append("<div class='name'>").append(esc(name)).append("</div>");
		String meta = joinHeaderMeta(headerParts);
		if (!meta.isEmpty()) {
			html.append("<div class='meta'>").append(esc(meta)).append("</div>");
		}
		html.append("</div>");

		ResumeSchemaDTO.Section summarySection = getSection(resume, "SUMMARY");
		if (summarySection != null && summarySection.getLines() != null && !summarySection.getLines().isEmpty()) {
			html.append("<div class='sec'>");
			html.append("<h2>Summary</h2>");
			String base = esc(summarySection.getLines().get(0));
			String enhanced = resumeAIEnhancerService.enhanceSummary(base, role, jd);
			html.append("<div>").append(esc(enhanced)).append("</div>");
			html.append("</div>");
		}

		if (resume != null && resume.getSections() != null) {
			for (ResumeSchemaDTO.Section section : resume.getSections()) {
				if (section == null || section.getTitle() == null)
					continue;
				if ("SUMMARY".equalsIgnoreCase(section.getTitle()))
					continue;
				if (section.getLines() == null || section.getLines().isEmpty())
					continue;

				String title = section.getTitle().trim();
				if ("PROJECTS".equalsIgnoreCase(title)) {
					renderProjects(html, section);
				} else {
				    html.append("<div class='sec'>");
				    html.append("<h2>").append(esc(title)).append("</h2>");

				    // ✅ Special handling for SKILLS
				    if ("SKILLS".equalsIgnoreCase(title)) {

				        html.append("<div class='skills-grid'>");

				        for (String line : section.getLines()) {
				            if (line == null || line.trim().isEmpty()) continue;

				            html.append("<div class='skill-item'>")
				                .append(esc(line))
				                .append("</div>");
				        }

				        html.append("</div>");
				    }

				    // 🔹 Other sections remain same
				    else {
				        html.append("<ul>");
				        for (String line : section.getLines()) {
				            if (line == null || line.trim().isEmpty()) continue;
				            html.append("<li>").append(esc(line)).append("</li>");
				        }
				        html.append("</ul>");
				    }

				    html.append("</div>");
				}
			}
		}

		html.append("</div>");
		html.append("</body></html>");

		return html.toString();

	}

	// ===== helpers =====

	private ResumeSchemaDTO.Section getSection(ResumeSchemaDTO resume, String... titles) {
		if (resume == null || resume.getSections() == null)
			return null;
		for (String t : titles) {
			for (ResumeSchemaDTO.Section s : resume.getSections()) {
				if (s != null && s.getTitle() != null && s.getTitle().equalsIgnoreCase(t))
					return s;
			}
		}
		return null;
	}

	private void renderProjects(StringBuilder html, ResumeSchemaDTO.Section section) {
	    html.append("<div class='sec'>");
	    html.append("<h2>Projects</h2>");

	    boolean projectOpen = false;

	    for (String raw : section.getLines()) {
	        if (raw == null) continue;

	        String line = raw.trim();
	        if (line.isEmpty()) continue;

	        // 🔹 New Project
	        if (line.contains("|")) {
	            if (projectOpen) {
	                html.append("</div>");
	            }

	            String[] parts = line.split("\\|", 2);

	            html.append("<div class='proj'>");

	            // ✅ Title + Tech inline
	            html.append("<div class='proj-title'>")
	                .append(esc(parts[0].trim()));

	            if (parts.length > 1 && !parts[1].trim().isEmpty()) {
	                html.append(" | ")
	                    .append("<span class='proj-tech'>")
	                    .append(esc(parts[1].trim()))
	                    .append("</span>");
	            }

	            html.append("</div>");

	            projectOpen = true;
	        }

	        // 🔹 Inside Project
	        else if (projectOpen) {

	            String cleanLine = line.replace("•", "").trim();

	            // ✅ ROLE (inline)
	            if (cleanLine.toLowerCase().startsWith("role")&&!cleanLine.toLowerCase().contains("description")) {

	                String roleValue = cleanLine.replace("Role:", "").trim();

	                html.append("<div style='margin-top:2mm;'>");
	                html.append("<strong>Role: </strong>")
	                    .append(esc(roleValue));
	                html.append("</div>");
	            }

	            // ✅ ROLE DESCRIPTION
	            else if (cleanLine.toLowerCase().startsWith("role description")) {

	            	String roleDesc = cleanLine.replace("Role Description:", "").trim();
	                html.append("<div style='margin-top:1mm;'>");
	                html.append("<strong>Role Description:</strong>");
	                html.append("<div>")
	                    .append(esc(roleDesc))
	                    .append("</div>");
	                html.append("</div>");
	            }

	            // ✅ DESCRIPTION
	            else {

	                html.append("<div style='margin-top:2mm;'>");
	                html.append("<strong>Description:</strong>");
	                html.append("<div>")
	                    .append(esc(cleanLine))
	                    .append("</div>");
	                html.append("</div>");
	            }
	        }
	    }

	    if (projectOpen) {
	        html.append("</div>");
	    }

	    html.append("</div>");
	}
	private String[] safeSplit(String header) {
		if (header == null)
			return new String[0];
		String[] parts = header.split("\\|");
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i] == null ? "" : parts[i].trim();
		}
		return parts;
	}

	private String joinHeaderMeta(String[] headerParts) {
		if (headerParts == null || headerParts.length <= 1)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < headerParts.length; i++) {
			String p = headerParts[i] == null ? "" : headerParts[i].trim();
			if (p.isEmpty())
				continue;
			if (sb.length() > 0)
				sb.append(" | ");
			sb.append(p);
		}
		return sb.toString();
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
				.replace("'", "&#39;");
	}
}
