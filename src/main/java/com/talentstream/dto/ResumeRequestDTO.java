package com.talentstream.dto;

import javax.validation.constraints.NotNull;

public class ResumeRequestDTO {
		@NotNull(message="Applicant id reuired")
	    private long applicantId;
	    private String resumeVersion; 
	    private String jd;
		public long getApplicantId() {
			return applicantId;
		}
		public void setApplicantId(long applicantId) {
			this.applicantId = applicantId;
		}
		public String getResumeVersion() {
			return resumeVersion;
		}
		public void setResumeVersion(String resumeVersion) {
			this.resumeVersion = resumeVersion;
		}
		public String getJd() {
			return jd;
		}
		public void setJd(String jd) {
			this.jd = jd;
		} 
	    

}
