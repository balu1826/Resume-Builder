package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talentstream.dto.FeedbackFormsResponseDTO;
import com.talentstream.entity.FeedbackFormsNew;

@Repository
public interface FeedbackFormsNewRepository extends JpaRepository<FeedbackFormsNew, Long> {

	@Query("SELECT f " + "FROM FeedbackFormsNew f " + "WHERE f.id = :formId "
			+ "AND f.recruiter.recruiterId = :recruiterId")
	FeedbackFormsNew findByIdAndRecruiterId(@Param("formId") Long formId, @Param("recruiterId") Long recruiterId);

	@Query("SELECT new com.talentstream.dto.FeedbackFormsResponseDTO("
			+ "f.id, f.mentorName, f.collegeName, f.title, f.isActive, f.createdAt" + ") " + "FROM FeedbackFormsNew f "
			+ "WHERE f.isActive = true " + "ORDER BY f.createdAt DESC")
	List<FeedbackFormsResponseDTO> findAllByIsActiveTrue();

	Optional<FeedbackFormsNew> findByIdAndIsActiveTrue(Long formId);

	@Query("SELECT new com.talentstream.dto.FeedbackFormsResponseDTO(" + "f.id, f.mentorName, f.collegeName, f.title, "
			+ "f.isActive, f.submissionCount, f.createdAt" + ") " + "FROM FeedbackFormsNew f "
			+ "WHERE f.recruiter.recruiterId = :recruiterId " + "ORDER BY f.createdAt DESC")
	List<FeedbackFormsResponseDTO> findAllByRecruiterId(@Param("recruiterId") Long recruiterId);

	@Query("SELECT new com.talentstream.dto.FeedbackFormsResponseDTO(" + "f.id, f.mentorName, f.collegeName, f.title, "
			+ "f.isActive, f.submissionCount, f.createdAt" + ") " + "FROM FeedbackFormsNew f "
			+ "ORDER BY f.createdAt DESC")
	List<FeedbackFormsResponseDTO> findAllForms();

	@Query("SELECT DISTINCT f.mentorName, f.collegeName " + "FROM FeedbackFormsNew f")
	List<Object[]> findAllMentorNamesAndCollegeNames();

	@Query("SELECT COUNT(f) > 0 " + "FROM FeedbackFormsNew f " + "WHERE LOWER(f.mentorName) = LOWER(:mentorName)")
	boolean existsByMentorNameIgnoreCase(@Param("mentorName") String mentorName);

	@Query("SELECT DISTINCT f.mentorName " + "FROM FeedbackFormsNew f")
	List<String> findAllDistinctMentorNames();

	@Query("SELECT COUNT(f) > 0 " + "FROM FeedbackFormsNew f " + "WHERE LOWER(f.mentorName) = LOWER(:mentorName) "
			+ "AND LOWER(f.collegeName) = LOWER(:collegeName)")
	boolean existsByMentorNameAndCollegeNameIgnoreCase(@Param("mentorName") String mentorName,
			@Param("collegeName") String collegeName);

	@Query("SELECT DISTINCT f.collegeName " + "FROM FeedbackFormsNew f "
			+ "WHERE LOWER(f.mentorName) = LOWER(:mentorName)")
	List<String> findDistinctCollegesByMentor(@Param("mentorName") String mentorName);

}
