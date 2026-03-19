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

@RestController
@RequestMapping("/api/resume")
public class ATSResumeController {

	private final SchemaFormatter formatter;
	private final ResumeHtmlRendererResolver htmlRendererResolver;
	private final ResumeService resumeService;
	private final PdfResumeRenderer pdfRenderer;

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

		// 2. Convert raw → ResumeSchemaDTO (NO mapping layer needed)
		ResumeSchemaDTO schema = formatter.format(raw);

		// 3. Convert schema → HTML
		ResumeHtmlRenderer renderer = htmlRendererResolver.resolve(request.getResumeVersion());

		String html = renderer.render(schema, raw.getSummary(), raw.getTitle(), request.getJd());
		System.out.println(html);
		// 4. Convert HTML → PDF bytes
		byte[] pdf = pdfRenderer.render(html);

		// 5. Return PDF
		return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=ATS_Resume.pdf").body(pdf);
	}

}