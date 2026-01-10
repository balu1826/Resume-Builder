package com.talentstream.ats;

import com.talentstream.dto.ATSResumeProfileDTO;
import com.talentstream.dto.ApplicantFullDataDTO;
import com.talentstream.dto.ResumeSchemaDTO;

public interface ATSFormatter {
    ResumeSchemaDTO format(ATSResumeProfileDTO profile);
    ResumeSchemaDTO format1(ApplicantFullDataDTO dto);

}
