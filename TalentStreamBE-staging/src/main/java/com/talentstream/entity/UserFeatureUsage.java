package com.talentstream.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "feature_tracker")
public class UserFeatureUsage {

	@Id
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "blogs_count", nullable = false, columnDefinition = "integer default 0")
	private int blogsCount = 0;

	@Column(name = "shorts_count", nullable = false, columnDefinition = "integer default 0")
	private int shortsCount = 0;

	@Column(name = "hackathons_count", nullable = false, columnDefinition = "integer default 0")
	private int hackathonsCount = 0;

	@Column(name = "ask_newton_count", nullable = false, columnDefinition = "integer default 0")
	private int askNewton = 0;

	@Column(name = "resume_upload_count", nullable = false, columnDefinition = "integer default 0")
	private int resumeUpload = 0;

	@Column(name = "blogs_count_mobile", nullable = false, columnDefinition = "integer default 0")
	private int mobileBlogsCount = 0;

	@Column(name = "shorts_count_mobile", nullable = false, columnDefinition = "integer default 0")
	private int mobileShortsCount = 0;

	@Column(name = "hackathons_count_mobile", nullable = false, columnDefinition = "integer default 0")
	private int mobileHackathonsCount = 0;

	@Column(name = "ask_newton_count_mobile", nullable = false, columnDefinition = "integer default 0")
	private int mobileAskNewton = 0;

	@Column(name = "resume_upload_count_mobile", nullable = false, columnDefinition = "integer default 0")
	private int mobileResumeUpload = 0;

	@Column(name = "mentor__sessions", nullable = false, columnDefinition = "integer default 0")
	private Integer mentorConnects = 0;

	@Column(name = "mobile_latest_session_date")
	private String mobileLatestSessionDate;

	@Column(name = "latest_session_date")
	private String latestSessionDate;

	@Column(name = "mobile_mentor__sessions", nullable = false, columnDefinition = "integer default 0")
	private Integer mobileMentorConnects = 0;
	// Dates
	@Column(name = "blogs_updated_at")
	private LocalDateTime blogsUpdatedAt;

	@Column(name = "shorts_updated_at")
	private LocalDateTime shortsUpdatedAt;

	@Column(name = "hackathons_updated_at")
	private LocalDateTime hackathonsUpdatedAt;

	@Column(name = "ask_newton_updated_at")
	private LocalDateTime askNewtonUpdatedAt;

	@Column(name = "resume_upload_updated_at")
	private LocalDateTime resumeUploadUpdatedAt;

	@Column(name = "mobile_blogs_updated_at")
	private LocalDateTime mobileBlogsUpdatedAt;

	@Column(name = "mobile_shorts_updated_at")
	private LocalDateTime mobileShortsUpdatedAt;

	@Column(name = "mobile_hackathons_updated_at")
	private LocalDateTime mobileHackathonsUpdatedAt;

	@Column(name = "mobile_ask_newton_updated_at")
	private LocalDateTime mobileAskNewtonUpdatedAt;

	@Column(name = "mobile_resume_upload_updated_at")
	private LocalDateTime mobileResumeUploadUpdatedAt;

	@Column(name = "mentor_sessions_updated_at")
	private LocalDateTime mentorSessionsUpdatedAt;

	@Column(name = "mobile_mentor_sessions_updated_at")
	private LocalDateTime mobileMentorSessionsUpdatedAt;

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setBlogsCount(int blogsCount) {
		this.blogsCount = blogsCount;
	}

	public void setShortsCount(int shortsCount) {
		this.shortsCount = shortsCount;
	}

	public void setHackathonsCount(int hackathonsCount) {
		this.hackathonsCount = hackathonsCount;
	}

	public void setMobileBlogsCount(int mobileBlogsCount) {
		this.mobileBlogsCount = mobileBlogsCount;
	}

	public void setMobileShortsCount(int mobileShortsCount) {
		this.mobileShortsCount = mobileShortsCount;
	}

	public void setMobileHackathonsCount(int mobileHackathonsCount) {
		this.mobileHackathonsCount = mobileHackathonsCount;
	}

	public void setMobileAskNewton(int mobileAskNewton) {
		this.mobileAskNewton = mobileAskNewton;
	}

	public void setMobileResumeUpload(int mobileResumeUpload) {
		this.mobileResumeUpload = mobileResumeUpload;
	}

	public void setMentorConnects(Integer mentorConnects) {
		this.mentorConnects = mentorConnects;
	}

	public void setMobileMentorConnects(Integer mobileMentorConnects) {
		this.mobileMentorConnects = mobileMentorConnects;
	}

	public void setBlogsUpdatedAt(LocalDateTime blogsUpdatedAt) {
		this.blogsUpdatedAt = blogsUpdatedAt;
	}

	public void setShortsUpdatedAt(LocalDateTime shortsUpdatedAt) {
		this.shortsUpdatedAt = shortsUpdatedAt;
	}

	public void setHackathonsUpdatedAt(LocalDateTime hackathonsUpdatedAt) {
		this.hackathonsUpdatedAt = hackathonsUpdatedAt;
	}

	public void setAskNewtonUpdatedAt(LocalDateTime askNewtonUpdatedAt) {
		this.askNewtonUpdatedAt = askNewtonUpdatedAt;
	}

	public void setResumeUploadUpdatedAt(LocalDateTime resumeUploadUpdatedAt) {
		this.resumeUploadUpdatedAt = resumeUploadUpdatedAt;
	}

	public void setMobileBlogsUpdatedAt(LocalDateTime mobileBlogsUpdatedAt) {
		this.mobileBlogsUpdatedAt = mobileBlogsUpdatedAt;
	}

	public void setMobileShortsUpdatedAt(LocalDateTime mobileShortsUpdatedAt) {
		this.mobileShortsUpdatedAt = mobileShortsUpdatedAt;
	}

	public void setMobileHackathonsUpdatedAt(LocalDateTime mobileHackathonsUpdatedAt) {
		this.mobileHackathonsUpdatedAt = mobileHackathonsUpdatedAt;
	}

	public void setMobileAskNewtonUpdatedAt(LocalDateTime mobileAskNewtonUpdatedAt) {
		this.mobileAskNewtonUpdatedAt = mobileAskNewtonUpdatedAt;
	}

	public void setMobileResumeUploadUpdatedAt(LocalDateTime mobileResumeUploadUpdatedAt) {
		this.mobileResumeUploadUpdatedAt = mobileResumeUploadUpdatedAt;
	}

	public void setMentorSessionsUpdatedAt(LocalDateTime mentorSessionsUpdatedAt) {
		this.mentorSessionsUpdatedAt = mentorSessionsUpdatedAt;
	}

	public void setMobileMentorSessionsUpdatedAt(LocalDateTime mobileMentorSessionsUpdatedAt) {
		this.mobileMentorSessionsUpdatedAt = mobileMentorSessionsUpdatedAt;
	}

	public String getMobileLatestSessionDate() {
		return mobileLatestSessionDate;
	}

	public void setMobileLatestSessionDate(String mobileLatestSessionDate) {
		this.mobileLatestSessionDate = mobileLatestSessionDate;
	}

	public int getResumeUpload() {
		return resumeUpload;
	}

	public void setResumeUpload(int resumeUpload) {
		this.resumeUpload = resumeUpload;
	}

	public void incrementResumeUpload() {
		this.resumeUpload++;
	}

	public int getAskNewton() {
		return askNewton;
	}

	public void setAskNewton(int askNewton) {
		this.askNewton = askNewton;
	}

	protected UserFeatureUsage() {
		// JPA requirement
	}

	public UserFeatureUsage(Long userId) {
		this.userId = userId;
	}

	public void incrementBlogs() {
		this.blogsCount++;
	}

	public void incrementShorts() {
		this.shortsCount++;
	}

	public void incrementHackathons() {
		this.hackathonsCount++;
	}

	public void incrementAskNewton() {
		this.askNewton++;
	}

	public Long getUserId() {
		return userId;
	}

	public int getBlogsCount() {
		return blogsCount;
	}

	public int getShortsCount() {
		return shortsCount;
	}

	public int getHackathonsCount() {
		return hackathonsCount;
	}

	public void incrementMobileBlogs() {
		this.mobileBlogsCount++;
	}

	public void incrementMobileShorts() {
		this.mobileShortsCount++;
	}

	public void incrementMobileHackathons() {
		this.mobileHackathonsCount++;
	}

	public void incrementMobileAskNewton() {
		this.mobileAskNewton++;
	}

	public void incrementMobileResumeUpload() {
		this.mobileResumeUpload++;
	}

	public void incrementMentorSessions() {
		this.mentorConnects++;
	}

	public void incrementMobileMentorSessions() {
		this.mobileMentorConnects++;
	}

	public String getLatestSessionDate() {
		return latestSessionDate;
	}

	public void setLatestSessionDate(String latestSessionDate) {
		this.latestSessionDate = latestSessionDate;
	}
}