package com.talentstream.service;

import com.talentstream.dto.FeedbackDTO;
import com.talentstream.entity.FeedbackField;
import com.talentstream.entity.FeedbackForm;
import com.talentstream.repository.FeedbackFieldRepository;
import com.talentstream.repository.FeedbackFormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackFormService {

    private final FeedbackFormRepository formRepo;
    private final FeedbackFieldRepository fieldRepo;

    public FeedbackFormService(FeedbackFormRepository formRepo, FeedbackFieldRepository fieldRepo) {
        this.formRepo = formRepo;
        this.fieldRepo = fieldRepo;
    }

    public List<FeedbackDTO> listForms() {
        return formRepo.findAll().stream()
                .map(FeedbackDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public FeedbackDTO getForm(Long id) {
        FeedbackForm form = formRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));
        return FeedbackDTO.fromEntity(form);
    }

    public FeedbackDTO createForm(FeedbackDTO dto) {
        FeedbackForm form = dto.toEntity();
        form.getFields().forEach(f -> f.setForm(form));
        FeedbackForm saved = formRepo.save(form);
        return FeedbackDTO.fromEntity(saved);
    }

    @Transactional
    public FeedbackDTO updateForm(Long id, FeedbackDTO req) {
        FeedbackForm form = formRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));

        // update meta
        form.setCollegeName(req.getCollegeName());
        form.setMentorName(req.getMentorName());
        form.setSessionTitle(req.getSessionTitle());

        // 1) Hard delete existing children in DB
        fieldRepo.deleteByFormId(form.getId());

        // 2) Clear the MANAGED collection (keep the same instance!)
        form.getFields().clear();

        // 3) Re-add new children, setting back-reference
        if (req.getFields() != null) {
        	for (com.talentstream.dto.FeedbackFieldDTO f : req.getFields())
 {
                FeedbackField nf = new FeedbackField();
                nf.setForm(form);
                nf.setLabel(f.getLabel());
                nf.setFieldType(f.getFieldType());
                nf.setOptions(f.getOptions());
                nf.setRequired(Boolean.TRUE.equals(f.getRequired()));
                form.getFields().add(nf);
            }
        }

        form.setUpdatedAt(java.time.LocalDateTime.now());
        FeedbackForm saved = formRepo.saveAndFlush(form);

        return FeedbackDTO.fromEntity(saved);
    }

    public void deleteForm(Long id) {
        formRepo.deleteById(id);
    }

    public FeedbackDTO getLatestOrNull() {
        return formRepo.findTopByOrderByCreatedAtDesc()
                .map(FeedbackDTO::fromEntity)
                .orElse(null);
    }

    public FeedbackDTO duplicate(Long id) {
        FeedbackForm existing = formRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));

        FeedbackForm copy = new FeedbackForm();
        copy.setCollegeName(existing.getCollegeName());
        copy.setMentorName(existing.getMentorName());
        copy.setSessionTitle(existing.getSessionTitle());
        copy.setFields(existing.getFields().stream().map(f -> {
            FeedbackField field = new FeedbackField();
            field.setLabel(f.getLabel());
            field.setFieldType(f.getFieldType());
            field.setRequired(f.isRequired());
            field.setOptions(f.getOptions());
            field.setForm(copy);
            return field;
        }).collect(Collectors.toList()));

        FeedbackForm saved = formRepo.save(copy);
        return FeedbackDTO.fromEntity(saved);
    }
}
