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
		html.append("<html>");
		html.append("<head>");
		html.append("<meta charset='UTF-8'/>");
		html.append("<title>Resume</title>");

		/* ===== PDF SAFE CSS ===== */
		html.append("<style>");
		html.append(
				"body {" +
				"font-family: Calibri, Arial, Helvetica, sans-serif;" +
				"font-size:12px;" +
				"color:#1f2937;" +
				"margin:0;" +
				"padding:0;" +
				"}");
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
		html.append("h1{font-size:22px;margin:0;text-transform:uppercase;}");
		html.append(".role{font-size:12px;color:#555;margin-top:4px;}");
		html.append(".contact{font-size:11px;color:#666;margin-top:6px;}");
		html.append("h2{font-size:11px;text-transform:uppercase;color:#94a3b8;margin:20px 0 8px 0;}");
		html.append("p{font-size:12px;line-height:1.6;margin:4px 0;}");
		html.append(".section{margin-bottom:12px;}");
		html.append(".left{width:30%;vertical-align:top;border-right:1px solid #e5e7eb;padding-right:15px;}");
		html.append(".right{width:70%;vertical-align:top;padding-left:15px;}");
		html.append(
				".skill{display:inline-block;background:#eff6ff;color:#2563eb;font-size:10px;padding:4px 6px;margin:2px;border-radius:3px;}");
		html.append(".project-title{font-weight:bold;font-size:12px;margin-bottom:4px;}");
		html.append(".edu-title{font-weight:bold;font-size:12px;}");
		html.append("ul{margin:6px 0 6px 15px;padding:0;}");
		html.append("li{font-size:12px;margin-bottom:4px;}");
		html.append(
				".right{width:70%;vertical-align:top;padding-left:15px;word-wrap:break-word;overflow-wrap:break-word;}");
		html.append("td{vertical-align:top;}");
		html.append("table{table-layout:fixed;}");
		
		html.append("</style>");

		html.append("</head>");
		html.append("<body>");
		html.append("<div class='wrapper'>");

		/* ===== HEADER ===== */
		String[] headerParts = resume.getHeader().split("\\|");

		html.append("<h1>").append(esc(headerParts[0])).append("</h1>");

		if (headerParts.length > 1) {
			html.append("<div class='role'>").append(esc(headerParts[1])).append("</div>");
		}

		for (int i = 2; i < headerParts.length; i++) {
			html.append("<div class='contact'>").append(esc(headerParts[i])).append("</div>");
		}

		/* ===== TWO COLUMN LAYOUT ===== */
		html.append("<table width='100%' height='100%' cellspacing='0' cellpadding='0' style='height:100%;border-collapse:collapse;'><tr>");
		/* ===== LEFT COLUMN ===== */
		html.append("<td class='left'>");

		/* PROFILE */
		ResumeSchemaDTO.Section summarySection = getSection(resume, "SUMMARY");
		if (summarySection != null && !summarySection.getLines().isEmpty()) {
			html.append("<div class='section'>");
			html.append("<h2>Profile</h2>");
			html.append("<p>")
					.append(resumeAIEnhancerService.enhanceSummary(esc(summarySection.getLines().get(0)), role, jd))
					.append("</p>");
			html.append("</div>");
		}

		/* SKILLS */
		ResumeSchemaDTO.Section skills = getSection(resume, "SKILLS");
		if (skills != null && !skills.getLines().isEmpty()) {
			html.append("<div class='section'>");
			html.append("<h2>Skills</h2>");
			int count = 0;

			for (String s : skills.getLines()) {

				if (count > 0 && count % 4 == 0) {
					html.append("<br/>"); // Force new row after 4 skills
				}

				html.append("<span class='skill'>")
						.append(esc(s))
						.append("</span>");

				count++;
			}
			html.append("</div>");
		}

		/* LANGUAGES */
		ResumeSchemaDTO.Section langs = getSection(resume, "LANGUAGES", "KNOWN LANGUAGES");
		if (langs != null && !langs.getLines().isEmpty()) {
			html.append("<div class='section'>");
			html.append("<h2>Languages</h2>");
			html.append("<ul>");
			for (String l : langs.getLines()) {
				html.append("<li>").append(esc(l)).append("</li>");
			}
			html.append("</ul>");
			html.append("</div>");
		}

		html.append("</td>");

		/* ===== RIGHT COLUMN ===== */
		html.append("<td class='right'>");

		/* PROJECTS */
		ResumeSchemaDTO.Section projects = getSection(resume, "PROJECTS");
		if (projects != null && projects.getLines() != null) {

			html.append("<div class='section'>");
			html.append("<h2>Projects</h2>");

			boolean projectOpen = false;

			for (String line : projects.getLines()) {

				if (line == null || line.trim().isEmpty())
					continue;

				if (line.contains("|")) {

					if (projectOpen) {
						html.append("</ul></div>");
					}

					String[] parts = line.split("\\|", 2);

					html.append("<div>");
					html.append("<div class='project-title'>")
							.append(esc(parts[0].trim()))
							.append("</div>");

					if (parts.length > 1) {
						html.append("<div style='font-size:10px;color:#64748b;margin-bottom:4px;'>")
								.append(esc(parts[1].trim()))
								.append("</div>");
					}

					html.append("<ul>");
					projectOpen = true;

				} else if (projectOpen) {

					html.append("<li>")
							.append(esc(line))
							.append("</li>");
				}
			}

			if (projectOpen) {
				html.append("</ul></div>");
			}

			html.append("</div>");
		}

		/* EDUCATION */
		ResumeSchemaDTO.Section edu = getSection(resume, "EDUCATION");
		if (edu != null && !edu.getLines().isEmpty()) {

			html.append("<div class='section'>");
			html.append("<h2>Education</h2>");

			for (String e : edu.getLines()) {
				html.append("<div style='margin-bottom:8px;'>")
						.append("<div class='edu-title'>")
						.append(esc(e))
						.append("</div>")
						.append("</div>");
			}

			html.append("</div>");
		}

		html.append("</td>");
		html.append("</tr></table>");

		html.append("</div>");
		html.append("</body>");
		html.append("</html>");

		return html.toString();

	}

	// ===== helpers =====

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
