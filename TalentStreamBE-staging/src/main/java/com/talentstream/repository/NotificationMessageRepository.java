package com.talentstream.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talentstream.dto.NotificationProjectionDTO;
import com.talentstream.entity.NotificationMessage;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {

	@Query(value = "SELECT n.id as id, " + "n.created_time as createdTime, " + "n.feature as feature, "
			+ "n.feature_id as featureId, " + "n.message as message, "
			+ "CAST(n.seen_applicant_id AS text) as seenApplicantId " + "FROM notification_message n "
			+ "WHERE n.applicant_id @> cast(:json as jsonb) " + "OR n.seen_applicant_id @> cast(:json as jsonb) "
			+ "ORDER BY n.created_time DESC",
			countQuery = "SELECT COUNT(*) FROM notification_message n "
					+ "WHERE n.applicant_id @> cast(:json as jsonb) "
					+ "OR n.seen_applicant_id @> cast(:json as jsonb)",
			nativeQuery = true)
	Page<NotificationProjectionDTO> findNotifications(@Param("json") String json, Pageable pageable);
	
	@Query(value =
	        "SELECT COUNT(*) " +
	        "FROM notification_message n " +
	        "WHERE n.applicant_id @> CAST(CONCAT('[', :applicantId, ']') AS jsonb)",
	        nativeQuery = true)
	int countUnreadNotifications(@Param("applicantId") Long applicantId);
}