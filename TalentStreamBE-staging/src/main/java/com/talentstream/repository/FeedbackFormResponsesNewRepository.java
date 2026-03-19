package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.FeedbackFormResponsesNew;

@Repository
public interface FeedbackFormResponsesNewRepository extends JpaRepository<FeedbackFormResponsesNew, Long> {

	Optional<FeedbackFormResponsesNew> findByFeedbackForm_IdAndApplicant_Id(Long feedbackFormId, Long applicantId);

	@Query("SELECT r " + "FROM FeedbackFormResponsesNew r " + "JOIN r.feedbackForm ff "
			+ "WHERE LOWER(ff.mentorName) = LOWER(:mentorName)")
	List<FeedbackFormResponsesNew> findByMentorName(@Param("mentorName") String mentorName);

	@Query("SELECT r " + "FROM FeedbackFormResponsesNew r " + "JOIN r.feedbackForm ff "
			+ "WHERE LOWER(ff.mentorName) = LOWER(:mentorName) " + "AND LOWER(ff.collegeName) = LOWER(:collegeName)")
	List<FeedbackFormResponsesNew> findByMentorNameAndCollegeName(@Param("mentorName") String mentorName,
			@Param("collegeName") String collegeName);
}
