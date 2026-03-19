package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.Applicant;
import com.talentstream.entity.StudentStreaks;

@Repository
public interface StudentStreaksRepository extends JpaRepository<StudentStreaks, Long> {

	Optional<StudentStreaks> findByApplicant(Applicant applicant);
	@Query("SELECT s FROM StudentStreaks s " +
		       "WHERE s.lastCompletedDate IS NULL " +
		       "OR s.lastCompletedDate <> CURRENT_DATE")
		List<StudentStreaks> findApplicantsWhoMissedToday();

	@Query(value = "SELECT CAST(attempted_dates AS TEXT) FROM student_streak WHERE applicant_id = ?1", nativeQuery = true)
	String findAttemptedDatesByApplicantId(Long applicantId);
}
