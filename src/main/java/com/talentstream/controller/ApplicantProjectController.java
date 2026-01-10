package com.talentstream.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.ProjectDetailsDTO;
import com.talentstream.service.ApplicantProjectService;

@CrossOrigin("*")
@RestController
@RequestMapping("/applicant-projects")
public class ApplicantProjectController {

    private final ApplicantProjectService projectService;

    public ApplicantProjectController(ApplicantProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{applicantId}/getApplicantProjects")
    public ResponseEntity<List<ProjectDetailsDTO>> getApplicantProjects(@PathVariable Long applicantId) {
        return ResponseEntity.ok(projectService.getApplicantProjects(applicantId));
    }
    
 // ApplicantProjectController.java

    @GetMapping("/{applicantId}/getApplicantCurrentProject")
    public ResponseEntity<ProjectDetailsDTO> getApplicantCurrentProject(@PathVariable Long applicantId) {
        return ResponseEntity.ok(projectService.getApplicantCurrentProject(applicantId));
    }


    @PutMapping("/{applicantId}/saveApplicantProject")
    public ResponseEntity<?> saveApplicantProject(
            @PathVariable Long applicantId,
            @Valid @RequestBody ProjectDetailsDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        String response = projectService.saveApplicantProject(applicantId, dto);
        return ResponseEntity.ok().body(response);
    }
}
