package com.talentstream.validation;
 
import com.talentstream.dto.CreateNewFeedBackFormQuestionDTO;
 
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
 
public class FixedFeedbackQuestionValidator
        implements ConstraintValidator<ValidFeedbackQuestion, CreateNewFeedBackFormQuestionDTO> {
 
    private static final Set<String> ALLOWED_DISPLAY_TYPES = Set.of(
            "EMOJIS", "STARS", "TEXT", "SCALE"
    );
 
    @Override
    public boolean isValid(CreateNewFeedBackFormQuestionDTO q,
                           ConstraintValidatorContext context) {
 
        if (q == null) {
            return true;
        }
 
        boolean valid = true;
        context.disableDefaultConstraintViolation();
 
        String type = q.getQuestionType();
        List<String> options = q.getOptions();
        String displayType = q.getDisplayType();
 
        if (type == null) {
            return true;
        }
 
        switch (type) {
 
            case "RADIO":
            case "CHECKBOX":
                valid &= requireOptions(
                        options,
                        2,
                        "For type " + type + ", options must contain at least 2 entries",
                        context
                );
 
                if (!isBlank(displayType)) {
                    context.buildConstraintViolationWithTemplate(
                            "displayType is only allowed for RATING type")
                           .addPropertyNode("displayType")
                           .addConstraintViolation();
                    valid = false;
                }
                break;
 
            case "RATING":
                if (isBlank(displayType)) {
                    context.buildConstraintViolationWithTemplate(
                            "displayType is required when questionType is RATING")
                           .addPropertyNode("displayType")
                           .addConstraintViolation();
                    valid = false;
                } else if (!ALLOWED_DISPLAY_TYPES.contains(displayType)) {
                    context.buildConstraintViolationWithTemplate(
                            "displayType must be one of: EMOJIS, STARS, TEXT, SCALE")
                           .addPropertyNode("displayType")
                           .addConstraintViolation();
                    valid = false;
                }
                break;
 
            default:
                if (options != null && !options.isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                            "Options are only allowed for RADIO, CHECKBOX types")
                           .addPropertyNode("options")
                           .addConstraintViolation();
                    valid = false;
                }
 
                if (!isBlank(displayType)) {
                    context.buildConstraintViolationWithTemplate(
                            "displayType is only allowed for RATING type")
                           .addPropertyNode("displayType")
                           .addConstraintViolation();
                    valid = false;
                }
        }
 
        return valid;
    }
 
    private boolean requireOptions(
            List<String> options,
            int minSize,
            String message,
            ConstraintValidatorContext context) {
 
        if (options == null || options.size() < minSize) {
            context.buildConstraintViolationWithTemplate(message)
                   .addPropertyNode("options")
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
 
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}