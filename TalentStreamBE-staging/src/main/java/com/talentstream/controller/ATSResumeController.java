package com.talentstream.controller;

import com.talentstream.ats.SchemaFormatter;
import com.talentstream.dto.ApplicantFullDataDTO;
import com.talentstream.dto.ResumeRequestDTO;
import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.ats.PdfResumeRenderer;
import com.talentstream.ats.ResumeHtmlRenderer;
import com.talentstream.ats.ResumeHtmlRendererResolver;

import com.talentstream.service.ResumeService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/resume")
public class ATSResumeController {

	private final SchemaFormatter formatter;
	private final ResumeHtmlRendererResolver htmlRendererResolver;
	private final ResumeService resumeService;
	private final PdfResumeRenderer pdfRenderer;
	
	 private static final Logger log = LoggerFactory.getLogger(ATSResumeController.class);

	public ATSResumeController(SchemaFormatter formatter, ResumeHtmlRendererResolver htmlRendererResolver,
			PdfResumeRenderer pdfRenderer, ResumeService resumeService) {
		this.formatter = formatter;
		this.htmlRendererResolver = htmlRendererResolver;
		this.pdfRenderer = pdfRenderer;
		this.resumeService = resumeService;
	}

	@PostMapping(value = "/download/resume", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> downloadResume(@RequestBody ResumeRequestDTO request) {

		// 1. Get raw data directly from service (your existing method)

		ApplicantFullDataDTO raw = resumeService.getFullApplicant(request.getApplicantId());
		 log.info("Applicant data fetched successfully for ID: {}", request.getApplicantId());
		// 2. Convert raw → ResumeSchemaDTO (NO mapping layer needed)
		ResumeSchemaDTO schema = formatter.format(raw);
		 log.info("Applicant data formatted successfully for ID: {}", request.getApplicantId());

		// 3. Convert schema → HTML
		ResumeHtmlRenderer renderer = htmlRendererResolver.resolve(request.getResumeVersion());
		 log.info("Resume template resolved  successfully for ID: {}", request.getApplicantId());

		String html = renderer.render(schema, raw.getSummary(), raw.getTitle(), request.getJd());
		 log.info("Resume rendered successfully for ID: {}", request.getApplicantId());
		
		// 4. Convert HTML → PDF bytes
		byte[] pdf = pdfRenderer.render(html);
		 log.info("PDF generated successfully for ID: {}", request.getApplicantId());

		// 5. Return PDF
		return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=ATS_Resume.pdf").body(pdf);
	}

}