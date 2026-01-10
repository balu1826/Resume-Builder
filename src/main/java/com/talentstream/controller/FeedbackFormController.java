package com.talentstream.controller;

import org.springframework.transaction.annotation.Transactional;
import com.talentstream.dto.*;
import com.talentstream.entity.FeedbackAnswer;
import com.talentstream.entity.FeedbackForm;
import com.talentstream.entity.FeedbackResponse;
import com.talentstream.repository.FeedbackFormRepository;
import com.talentstream.repository.FeedbackResponseRepository;
import com.talentstream.service.FeedbackFormService;


import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mentorfeedback")
public class FeedbackFormController {

    private final FeedbackFormService formService;
    private final FeedbackFormRepository formRepo;
    private final FeedbackResponseRepository responseRepo;

    public FeedbackFormController(FeedbackFormService formService,
                                  FeedbackFormRepository formRepo,
                                  FeedbackResponseRepository responseRepo) {
        this.formService = formService;
        this.formRepo = formRepo;
        this.responseRepo = responseRepo;
    }

    // --- Admin/Builder ---
    @GetMapping("/forms")
    public ResponseEntity<List<FeedbackDTO>> listForms() {
        return ResponseEntity.ok(formService.listForms());
    }

    @GetMapping("/forms/{id}")
    public ResponseEntity<FeedbackDTO> getForm(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getForm(id));
    }

    @PostMapping("/forms")
    public ResponseEntity<FeedbackDTO> createForm(@RequestBody @Valid FeedbackDTO req) {
        return ResponseEntity.ok(formService.createForm(req));
    }

    @PutMapping("/forms/{id}")
    public ResponseEntity<FeedbackDTO> updateForm(
            @PathVariable Long id,
            @RequestBody @Valid FeedbackDTO req) {
        return ResponseEntity.ok(formService.updateForm(id, req));
    }


    @DeleteMapping("/forms/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        formService.deleteForm(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forms/{id}/duplicate")
    public ResponseEntity<FeedbackDTO> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(formService.duplicate(id));
    }

    // --- Public/Student ---
    @GetMapping("/form")
    public ResponseEntity<FeedbackDTO> getDefaultForm() {
        FeedbackDTO dto = formService.getLatestOrNull();
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/form/{id}")
    public ResponseEntity<FeedbackDTO> getFormById(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getForm(id));
    }

    @PostMapping("/form/{id}/submit")
    public ResponseEntity<Long> submitFeedback(@PathVariable Long id,
                                               @RequestBody @Valid SubmitFeedbackRequest req) {
        FeedbackForm form = formRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));

        FeedbackResponse r = new FeedbackResponse();
        r.setForm(form);

        // map answers
        req.getAnswers().forEach((label, val) -> {
            FeedbackAnswer a = new FeedbackAnswer();
            a.setLabel(label);
            a.setValue(val == null ? "" : String.valueOf(val));
            a.setResponse(r);
            r.getAnswers().add(a);
        });
        Long responseId = responseRepo.save(r).getId();
        return ResponseEntity.ok(responseId);
    }

    @GetMapping("/form/{id}/responses")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FeedbackResponseDTO>> getResponses(@PathVariable Long id) {
        FeedbackForm form = formRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));

        List<FeedbackResponseDTO> list = responseRepo.findByFormIdOrderByCreatedAtDesc(form.getId())
                .stream()
                .map(r -> {
                    FeedbackResponseDTO dto = new FeedbackResponseDTO();
                    dto.setId(r.getId());
                    dto.setFormId(form.getId());
                    dto.setCreatedAt(r.getCreatedAt());

                    Map<String, String> answers = new LinkedHashMap<>();
                    r.getAnswers().forEach(a -> answers.put(a.getLabel(), a.getValue()));
                    dto.setAnswers(answers);

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }
}
