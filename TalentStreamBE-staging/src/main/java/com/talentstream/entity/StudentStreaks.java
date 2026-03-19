package com.talentstream.entity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

@Entity
@Table(name = "student_streak", uniqueConstraints = {
		@UniqueConstraint(name = "uk_student_streak_applicant", columnNames = "applicant_id") })
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class StudentStreaks {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "applicant_id", nullable = false, unique = true)
	private Applicant applicant;

	@Column(name = "current_streak", nullable = false)
	private int currentStreak = 0;

	@Column(name = "longest_streak", nullable = false)
	private int longestStreak = 0;

	@Column(name = "last_completed_date")
	private LocalDate lastCompletedDate;

	@Column(name = "monthly_restore_used", nullable = false)
	private int monthlyRestoreUsed = 0;

	// Stored as String (YYYY-MM)
	@Column(name = "restore_month", length = 7)
	private String restoreMonth;

	@Type(type = "jsonb")
	@Column(name = "attempted_dates", columnDefinition = "jsonb")
	private List<LocalDate> attemptedDates = new ArrayList<>();

	// Constructors
	public StudentStreaks() {
	}

	public StudentStreaks(Applicant applicant) {
		this.applicant = applicant;
		this.restoreMonth = YearMonth.now().toString();
		this.attemptedDates = new ArrayList<>();
	}

	// Convert restoreMonth safely
	public YearMonth getRestoreMonth() {
		return restoreMonth != null ? YearMonth.parse(restoreMonth) : null;
	}

	public void setRestoreMonth(YearMonth restoreMonth) {
		this.restoreMonth = restoreMonth != null ? restoreMonth.toString() : null;
	}

	// Getters & Setters

	public Long getId() {
		return id;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public int getCurrentStreak() {
		return currentStreak;
	}

	public void setCurrentStreak(int currentStreak) {
		this.currentStreak = currentStreak;
	}

	public int getLongestStreak() {
		return longestStreak;
	}

	public void setLongestStreak(int longestStreak) {
		this.longestStreak = longestStreak;
	}

	public LocalDate getLastCompletedDate() {
		return lastCompletedDate;
	}

	public void setLastCompletedDate(LocalDate lastCompletedDate) {
		this.lastCompletedDate = lastCompletedDate;
	}

	public int getMonthlyRestoreUsed() {
		return monthlyRestoreUsed;
	}

	public void setMonthlyRestoreUsed(int monthlyRestoreUsed) {
		this.monthlyRestoreUsed = monthlyRestoreUsed;
	}

	public List<LocalDate> getAttemptedDates() {
		return attemptedDates;
	}

	public void setAttemptedDates(List<LocalDate> attemptedDates) {
		this.attemptedDates = attemptedDates;
	}
}