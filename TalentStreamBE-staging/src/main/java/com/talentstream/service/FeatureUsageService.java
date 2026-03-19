package com.talentstream.service;
 
import java.time.LocalDateTime;
 
import javax.transaction.Transactional;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
 
import com.talentstream.dto.AnalyticsEventRequest;
import com.talentstream.entity.UserFeatureUsage;
import com.talentstream.repository.UserFeatureUsageRepository;
 
@Service
public class FeatureUsageService {
 
	private static final Logger logger = LoggerFactory.getLogger(FeatureUsageService.class);
 
	private final UserFeatureUsageRepository repo;
 
	public FeatureUsageService(UserFeatureUsageRepository repo) {
		this.repo = repo;
	}
 
	@Transactional
	public void recordFeatureEvent(AnalyticsEventRequest request) {
 
		try {
 
			long userId = request.getUserId();
			String feature = request.getFeature();
 
			logger.info("Recording feature event | userId={} | feature={}", userId, feature);
 
			UserFeatureUsage usage = repo.findByUserId(userId).orElse(null);
 
			if (usage == null) {
				logger.info("No existing usage found. Creating new record for userId={}", userId);
 
				usage = new UserFeatureUsage(userId);
				usage.setCreatedAt(LocalDateTime.now());
				updateCount(usage, feature);
				repo.save(usage);
 
				logger.info("New feature usage record saved for userId={}", userId);
				return;
			}
 
			updateCount(usage, feature);
			repo.save(usage);
 
			logger.info("Feature usage updated successfully for userId={}", userId);
 
		} catch (IllegalArgumentException e) {
 
			logger.error("Invalid feature provided: {}", e.getMessage());
			throw e;
 
		} catch (Exception e) {
 
			logger.error("Error while recording feature usage", e);
			throw new RuntimeException("Failed to record feature usage", e);
		}
	}
 
	private void updateCount(UserFeatureUsage usage, String feature) {
 
		try {
 
			logger.debug("Updating feature count | feature={}", feature);
 
			if (feature.indexOf("MENTOR") != -1 && feature.indexOf("MOBILE") != -1) {
 
				String date = feature.substring(feature.lastIndexOf(" ") + 1);
 
				logger.debug("Mobile mentor session detected | date={}", date);
 
				if (usage.getMobileLatestSessionDate() != null && usage.getMobileLatestSessionDate().equals(date)) {
 
					logger.info("Duplicate mobile mentor session ignored for date={}", date);
					return;
				}
 
				usage.incrementMobileMentorSessions();
				usage.setMobileLatestSessionDate(date);
				usage.setMobileMentorSessionsUpdatedAt(LocalDateTime.now());
 
				logger.info("Mobile mentor session incremented | date={}", date);
				return;
			}
 
			else if (feature.indexOf("MENTOR") != -1) {
 
				String date = feature.substring(feature.lastIndexOf(" ") + 1);
 
				logger.debug("Web mentor session detected | date={}", date);
 
				if (usage.getLatestSessionDate() != null && usage.getLatestSessionDate().equals(date)) {
 
					logger.info("Duplicate mentor session ignored for date={}", date);
					return;
				}
 
				usage.incrementMentorSessions();
				usage.setLatestSessionDate(date);
				usage.setMentorSessionsUpdatedAt(LocalDateTime.now());
 
				logger.info("Mentor session incremented | date={}", date);
				return;
			}
 
			switch (feature) {
 
			case "BLOGS":
				usage.incrementBlogs();
				usage.setBlogsUpdatedAt(LocalDateTime.now());
				logger.info("Blogs count incremented");
				break;
 
			case "SHORTS":
				usage.incrementShorts();
				usage.setShortsUpdatedAt(LocalDateTime.now());
				logger.info("Shorts count incremented");
				break;
 
			case "HACKATHONS":
				usage.incrementHackathons();
				usage.setHackathonsUpdatedAt(LocalDateTime.now());
				logger.info("Hackathons count incremented");
				break;
 
			case "ASK NEWTON":
				usage.incrementAskNewton();
				usage.setAskNewtonUpdatedAt(LocalDateTime.now());
				logger.info("Ask Newton count incremented");
				break;
 
			case "RESUME UPLOAD":
				usage.incrementResumeUpload();
				usage.setResumeUploadUpdatedAt(LocalDateTime.now());
				logger.info("Resume upload count incremented");
				break;
 
			case "MOBILE-BLOGS":
				usage.incrementMobileBlogs();
				usage.setMobileBlogsUpdatedAt(LocalDateTime.now());
				logger.info("Mobile blogs count incremented");
				break;
 
			case "MOBILE-SHORTS":
				usage.incrementMobileShorts();
				usage.setMobileShortsUpdatedAt(LocalDateTime.now());
				logger.info("Mobile shorts count incremented");
				break;
 
			case "MOBILE-HACKATHONS":
				usage.incrementMobileHackathons();
				usage.setMobileHackathonsUpdatedAt(LocalDateTime.now());
				logger.info("Mobile hackathons count incremented");
				break;
 
			case "MOBILE-ASK NEWTON":
				usage.incrementMobileAskNewton();
				usage.setMobileAskNewtonUpdatedAt(LocalDateTime.now());
				logger.info("Mobile Ask Newton count incremented");
				break;
 
			case "MOBILE-RESUME UPLOAD":
				usage.incrementMobileResumeUpload();
				usage.setMobileResumeUploadUpdatedAt(LocalDateTime.now());
				logger.info("Mobile resume upload count incremented");
				break;
 
			default:
				logger.error("Invalid feature received: {}", feature);
				throw new IllegalArgumentException("Invalid feature: " + feature);
			}
 
		} catch (Exception e) {
 
			logger.error("Error updating feature count for feature={}", feature, e);
			throw e;
		}
	}
}