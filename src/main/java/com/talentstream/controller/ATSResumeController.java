package com.talentstream.controller;

import com.talentstream.ats.ATSFormatter;
import com.talentstream.ats.ATSFormatterResolver;
import com.talentstream.dto.ATSResumeProfileDTO;
import com.talentstream.dto.ApplicantFullDataDTO;
import com.talentstream.dto.ResumeRequestDTO;
import com.talentstream.dto.ResumeSchemaDTO;
import com.talentstream.ats.HtmlResumeRenderer;
import com.talentstream.ats.PdfResumeRenderer;
import com.talentstream.service.ATSResumeProfileAdapter;
import com.talentstream.service.ResumeService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
public class ATSResumeController {

    private final ATSResumeProfileAdapter adapter;
    private final ATSFormatterResolver resolver;
    private final HtmlResumeRenderer renderer;
    private final ResumeService resumeService;
    PdfResumeRenderer pdfRenderer;

    public ATSResumeController(
            ATSResumeProfileAdapter adapter,
            ATSFormatterResolver resolver,
            HtmlResumeRenderer renderer,
            PdfResumeRenderer pdfRenderer,
            ResumeService resumeService
    ) {
        this.adapter = adapter;
        this.resolver = resolver;
        this.renderer = renderer;
        this.pdfRenderer=pdfRenderer;
        this.resumeService=resumeService;
    }

   
    
    
 
    	
    	
    	@GetMapping("/getRaw/{applicantId}")
    	public ApplicantFullDataDTO getRaw(@PathVariable Long applicantId) {
    		return resumeService.getFullApplicant(applicantId);
    	}
    	
    
    		
    		@GetMapping(
        		    value = "/download/resume",
        		    produces = MediaType.APPLICATION_PDF_VALUE
        		)
        		public ResponseEntity<byte[]> downloadResume(@RequestBody
        		      ResumeRequestDTO request
        		) {

        		    // 1. Get raw data directly from service (your existing method)
        			
        		    ApplicantFullDataDTO raw = resumeService.getFullApplicant(request.getApplicantId());
        		 
        		    // 2. Get formatter based on version
        		    ATSFormatter formatter = resolver.resolve(request.getResumeVersion());

        		    // 3. Convert raw → ResumeSchemaDTO (NO mapping layer needed)
        		    ResumeSchemaDTO schema = formatter.format1(raw);
        		   

        		    // 4. Convert schema → HTML
        		    String html = renderer.render(schema,raw.getSummary(),raw.getTitle(),request.getJd());
        		   

        		    // 5. Convert HTML → PDF bytes
        		    byte[] pdf = pdfRenderer.render(html);

        		    // 6. Return PDF
        		    return ResponseEntity.ok()
        		            .header("Content-Disposition", "attachment; filename=ATS_Resume.pdf")
        		            .body(pdf);
        		}
        		
        		
    		
    		


}
