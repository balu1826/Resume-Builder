package com.talentstream.ats;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
@Component
public class ATSFormatterResolver {

    private final Map<Integer, ATSFormatter> formatterMap = new HashMap<>();

    public ATSFormatterResolver(
            ATSV1Formatter v1Formatter,
            ATSV2Formatter v2Formatter) {

        formatterMap.put(1, v1Formatter);
        formatterMap.put(2, v2Formatter);
    }

    public ATSFormatter resolve(int templateType) {
        ATSFormatter formatter = formatterMap.get(templateType);
        if (formatter == null) {
            throw new IllegalArgumentException(
                    "Unsupported template type: " + templateType
            );
        }
        return formatter;
    }
}
