package com.talentstream.dto;

import javax.validation.constraints.NotNull;

public class ResumeRequestDTO {
		@NotNull(message="Applicant id reuired")
	    private Long applicantId;
	    private int resumeVersion; 
	    private String jd;
		public long getApplicantId() {
			return applicantId;
		}
		public void setApplicantId(long applicantId) {
			this.applicantId = applicantId;
		}
		public int getResumeVersion() {
			return resumeVersion;
		}
		public void setResumeVersion(int resumeVersion) {
			this.resumeVersion = resumeVersion;
		}
		public String getJd() {
			return jd;
		}
		public void setJd(String jd) {
			this.jd = jd;
		} 
	    

}