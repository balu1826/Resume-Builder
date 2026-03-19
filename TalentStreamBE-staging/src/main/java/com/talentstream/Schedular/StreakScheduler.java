package com.talentstream.Schedular;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.talentstream.entity.StudentStreaks;
import com.talentstream.repository.StudentStreaksRepository;
import com.talentstream.service.FirebaseMessagingService;
import com.talentstream.service.NotificationMessageService;
import com.talentstream.service.StreakService;

@Component
@EnableScheduling
public class StreakScheduler {

	private static final Logger logger = LoggerFactory.getLogger(StreakScheduler.class);

	private final StreakService streakService;

	private final FirebaseMessagingService firebaseMessagingService;

	private final StudentStreaksRepository studentStreaksRepository;
	private final NotificationMessageService notificationMessageService;

	public StreakScheduler(StreakService streakService, FirebaseMessagingService firebaseMessagingService, StudentStreaksRepository studentStreaksRepository, NotificationMessageService notificationMessageService) {
		this.streakService = streakService;
		this.firebaseMessagingService = firebaseMessagingService;
		this.studentStreaksRepository = studentStreaksRepository;
		this.notificationMessageService = notificationMessageService;
	}

	@Scheduled(cron = "0 0 6,18 * * ?")
	public void generateDailyQuestions() {
		logger.info("Streak Scheduler started");

		try {
			String result = streakService.generateQuestions();
			logger.info("Scheduler result: {}", result);
		} catch (Exception e) {
			logger.error("Error while generating questions in scheduler", e);
		}

		logger.info("Streak Scheduler finished");
	}

	@Scheduled(cron = "0 0 11 * * ?", zone = "Asia/Kolkata")
	public void morningReminder() {
		streakService.sendDailyStreakReminders("MORNING");
	}

	@Scheduled(cron = "0 0 20 * * ?", zone = "Asia/Kolkata")
	public void eveningReminder() {
		streakService.sendDailyStreakReminders("EVENING");
	}

	@Scheduled(cron = "0 0 16 * * ?", zone = "Asia/Kolkata") 
	public void notificationReminder() {

	    List<StudentStreaks> missedList = studentStreaksRepository.findApplicantsWhoMissedToday();

	    if (missedList.isEmpty()) {
	        return;
	    }

	    // Collect applicant IDs once
	    List<Long> applicantIds = missedList.stream()
	            .map(streak -> streak.getApplicant().getId())
	            .collect(Collectors.toList());

	    String title = "⏰ 4 PM Reminder: Don’t Break Your Streak!";
	    String message = "You haven’t completed today’s test yet. Attempt now and keep your streak alive 🔥";

	    // Save as In-App Notification (DB)
	    notificationMessageService.sendNotificationToApplicants(
	            title,
	            message,
	            null,
	            applicantIds
	    );

	    // Send Push Notification (FCM)
	    firebaseMessagingService.sendNotificationToStreakMaintainers(
	            title,
	            message,
	            applicantIds
	    );
	}
}
