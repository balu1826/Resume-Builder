package com.talentstream.ats;

import org.springframework.stereotype.Component;

@Component
public class ATSFormatterResolver {

    private final ATSFormatter atsV1Formatter;

    public ATSFormatterResolver(ATSFormatter atsV1Formatter) {
        this.atsV1Formatter = atsV1Formatter;
    }

    public ATSFormatter resolve(String version) {

        // Future-ready switch
        if ("V1".equalsIgnoreCase(version)) {
            return atsV1Formatter;
        }

        // default fallback
        return atsV1Formatter;
    }
}
