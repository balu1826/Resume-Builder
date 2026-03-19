package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.UserFcmTokens;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.UserFcmTokensRepository;

@Service
public class FirebaseMessagingService {

	@Autowired
	private UserFcmTokensRepository fcmTokensRepository;

	@Autowired
	private ApplicantRepository applicantRepository;

	private static final Logger logger = LoggerFactory.getLogger(FirebaseMessagingService.class);

	public void saveFcmToken(UserFcmTokens fcmTokens, Long id) throws Exception {
		logger.debug("saveFcmToken - entry applicantId={}", id);
		Applicant applicant = applicantRepository.findById(id)
				.orElseThrow(() -> new Exception("Applicant Not Found with id: " + id));

		List<UserFcmTokens> existingTokens = fcmTokensRepository.findByApplicant_Id(id);
		for (UserFcmTokens existing : existingTokens) {
			if (existing.getFcmToken().equals(fcmTokens.getFcmToken())) {
				logger.warn("saveFcmToken - duplicate token for applicantId={}", id);
				throw new CustomException("Same Token Already Present for Applicant", HttpStatus.BAD_REQUEST);
			}
		}

		fcmTokens.setApplicant(applicant);
		fcmTokens.setCreatedAt(LocalDateTime.now());
		fcmTokensRepository.save(fcmTokens);
		logger.info("saveFcmToken - FCM token saved for applicantId={}", id);
	}

	public List<UserFcmTokens> getUserActiveFcmTokenById(Long id) {
		logger.debug("getUserActiveFcmTokenById - entry applicantId={}", id);
		List<UserFcmTokens> fcmTokens = fcmTokensRepository.findByApplicant_IdAndIsTokenActiveTrue(id);
		if (fcmTokens == null || fcmTokens.isEmpty()) {
			logger.warn("getUserActiveFcmTokenById - no active tokens found for applicantId={}", id);
			throw new CustomException("Fcm Token Not found", HttpStatus.NOT_FOUND);
		}
		logger.debug("getUserActiveFcmTokenById - found {} active tokens for applicantId={}", fcmTokens.size(), id);
		return fcmTokens;
	}

	public String sendNotification(Long applicantId, String title, String body) throws Exception {
		logger.debug("sendNotification - entry applicantId={}, title={}", applicantId, title);
		List<UserFcmTokens> tokens = getUserActiveFcmTokenById(applicantId);
		logger.debug("sendNotification - sending to {} tokens for applicantId={}", tokens.size(), applicantId);
		return sendToTokens(tokens, title, body);
	}

	@Async
	public void sendNotificationToAll(String title, String body) {
		logger.debug("sendNotificationToAll - entry title={}", title);
		List<UserFcmTokens> tokens = fcmTokensRepository.findByIsTokenActiveTrue();

		if (tokens == null || tokens.isEmpty()) {
			logger.warn("sendNotificationToAll - no active FCM tokens found");
			return;
		}

		logger.info("sendNotificationToAll - sending to {} active tokens", tokens.size());

		try {
			sendToTokens(tokens, title, body);
		} catch (Exception e) {
			logger.error("sendNotificationToAll - error: {}", e.getMessage(), e);
		}
	}

	private String sendToTokens(List<UserFcmTokens> userFcmTokens, String title, String body) throws Exception {
		logger.debug("sendToTokens - entry tokenCount={}, title={}", userFcmTokens.size(), title);

		// Notification notification =
		// Notification.builder().setTitle(title).setBody(body).build();

		StringBuilder result = new StringBuilder();
		int successCount = 0;
		int failureCount = 0;

		for (UserFcmTokens userFcmToken : userFcmTokens) {
			String token = userFcmToken.getFcmToken();
			String mail = userFcmToken.getApplicant().getEmail();

			if (token == null || token.trim().isEmpty()) {
				logger.warn("sendToTokens - skipping empty token");
				continue;
			}

			String personalizedTitle = (mail != null ? "Hi " + mail + ", " : "") + title;

			Notification notification = Notification.builder().setTitle(personalizedTitle).setBody(body).build();
			Message message = Message.builder().setToken(token).setNotification(notification).build();

			try {
				String response = FirebaseMessaging.getInstance().send(message);
				result.append("Successfully sent to token: ").append(token.substring(0, Math.min(10, token.length())))
						.append("... => Response: ").append(response).append("\n");
				successCount++;
				logger.debug("sendToTokens - sent to token (truncated): {}...",
						token.substring(0, Math.min(10, token.length())));
			} catch (FirebaseMessagingException fme) {
				logger.error("sendToTokens - Firebase error: {}", fme.getMessage());

				if (fme.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
						|| fme.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
					logger.warn("sendToTokens - deactivating invalid token");
					userFcmToken.setIsTokenActive(false);
					fcmTokensRepository.save(userFcmToken);
				}
				failureCount++;
			} catch (Exception e) {
				logger.error("sendToTokens - General error: {}", e.getMessage());
				failureCount++;
			}
		}

		logger.info("sendToTokens - completed: sent={}, failed={}", successCount, failureCount);

		if (successCount == 0 && failureCount > 0) {
			throw new Exception("Failed to send any notifications");
		}

		return result.toString();
	}

	public UserFcmTokens getToken(String fcmToken) {
		logger.debug("getToken - entry fcmToken={}", fcmToken);
		UserFcmTokens token = fcmTokensRepository.findByFcmToken(fcmToken)
				.orElseThrow(() -> new NoSuchElementException("FCM Token not found: " + fcmToken));
		logger.debug("getToken - found token fcmToken={}", fcmToken);
		return token;
	}

	public String muteToken(String fcmToken) {
		logger.debug("muteToken - entry fcmToken={}", fcmToken);
		UserFcmTokens token = getToken(fcmToken);

		if (!token.getIsTokenActive()) {
			logger.warn("muteToken - token already muted fcmToken={}", fcmToken);
			throw new IllegalStateException("Notification already muted for this token");
		}

		token.setIsTokenActive(false);
		fcmTokensRepository.save(token);
		logger.info("muteToken - token muted fcmToken={}", fcmToken);

		return "Notification muted successfully";
	}

	public String unmuteToken(String fcmToken) {
		logger.debug("unmuteToken - entry fcmToken={}", fcmToken);
		UserFcmTokens token = getToken(fcmToken);

		if (token.getIsTokenActive()) {
			logger.warn("unmuteToken - token already unmuted fcmToken={}", fcmToken);
			throw new IllegalStateException("Notification already unmuted for this token");
		}

		token.setIsTokenActive(true);
		fcmTokensRepository.save(token);
		logger.info("unmuteToken - token unmuted fcmToken={}", fcmToken);

		return "Notification unmuted successfully";
	}

	public void setTokenStatusFalseForApplicant(Long id) {
		logger.debug("setTokenStatusFalseForApplicant - entry applicantId={}", id);
		List<UserFcmTokens> tokens = fcmTokensRepository.findByApplicant_Id(id);
		if (tokens == null || tokens.isEmpty()) {
			logger.warn("setTokenStatusFalseForApplicant - no tokens found for applicantId={}", id);
			return;
		}

		for (UserFcmTokens token : tokens) {
			token.setIsTokenActive(false);
			fcmTokensRepository.save(token);
			logger.debug("setTokenStatusFalseForApplicant - deactivated token for applicantId={}", id);
		}
	}

	@Async
	public void sendNotificationToStreakMaintainers(String title,
	                                                String message,
	                                                List<Long> applicantIds) {
	    logger.debug("sendNotificationToStreakMaintainers - entry applicantCount={}",
	            applicantIds != null ? applicantIds.size() : 0);

	    if (applicantIds == null || applicantIds.isEmpty()) {
	        logger.warn("sendNotificationToStreakMaintainers - applicantIds empty");
	        return;
	    }
	    // Fetch only active tokens for those applicants
	    List<UserFcmTokens> tokens =
	            fcmTokensRepository.findByApplicant_IdInAndIsTokenActiveTrue(applicantIds);
	    if (tokens == null || tokens.isEmpty()) {
	        logger.warn("sendNotificationToStreakMaintainers - no active tokens found");
	        return;
	    }
	    logger.info("sendNotificationToStreakMaintainers - sending to {} tokens", tokens.size());
	    try {
	        sendToTokens(tokens, title, message);
	    } catch (Exception e) {
	        logger.error("sendNotificationToStreakMaintainers - error: {}", e.getMessage(), e);
	    }
	}
}