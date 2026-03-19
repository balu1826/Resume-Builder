package com.talentstream.ats;

import com.talentstream.dto.ResumeSchemaDTO;

public interface ResumeHtmlRenderer {

    String render(ResumeSchemaDTO resume,
                  String summary,
                  String role,
                  String jd);
}