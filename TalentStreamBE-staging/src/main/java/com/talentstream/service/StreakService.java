package com.talentstream.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.config.AiConfig;
import com.talentstream.dto.StreakQuestionsDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.StreakQuestions;
import com.talentstream.entity.StudentStreaks;
import com.talentstream.exception.CustomException;
import com.talentstream.providers.AiProvider;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.PromptsRepository;
import com.talentstream.repository.StreakQuestionsRepository;
import com.talentstream.repository.StudentStreaksRepository;

@Service
public class StreakService {

	private static final Logger logger = LoggerFactory.getLogger(StreakService.class);
	private static final int DAILY_LIMIT = 5;
	private static final int MAX_MONTHLY_RESTORE = 2;

	private final PromptsRepository promptsRepository;
	private final Map<String, AiProvider> aiProviders;
	private final AiConfig aiConfig;
	private final StreakQuestionsRepository streakQuestionsRepository;
	private final ObjectMapper mapper;
	private final StudentStreaksRepository studentStreaksRepository;
	private final ApplicantRepository applicantRepository;
	private final EmailService emailService;

	public StreakService(PromptsRepository promptsRepository, Map<String, AiProvider> aiProviders, AiConfig aiConfig,
			StreakQuestionsRepository streakQuestionsRepository, ObjectMapper mapper,
			StudentStreaksRepository studentStreaksRepository, ApplicantRepository applicantRepository,
			EmailService emailService) {

		this.promptsRepository = promptsRepository;
		this.aiProviders = aiProviders;
		this.aiConfig = aiConfig;
		this.streakQuestionsRepository = streakQuestionsRepository;
		this.mapper = mapper;
		this.studentStreaksRepository = studentStreaksRepository;
		this.applicantRepository = applicantRepository;
		this.emailService = emailService;
	}

	// QUESTION GENERATION
	@Transactional
	public String generateQuestions() {

		String prompt = promptsRepository.findByFeature("MINIMOCK").getPrompt();
		if (prompt == null || prompt.isEmpty()) {
			throw new CustomException("Prompt not configured", HttpStatus.NOT_FOUND);
		}

		AiProvider aiProvider = getAiProvider();

		List<StreakQuestionsDTO> dtos = fetchValidQuestions(prompt, aiProvider);

		if (dtos.isEmpty()) {
			throw new CustomException("AI returned empty list", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<StreakQuestions> newEntities = filterNewQuestions(dtos);
		assignPostDates(newEntities);

		streakQuestionsRepository.saveAll(newEntities);
		logger.info("Successfully saved {} new questions to database", newEntities.size());

		return "Saved " + newEntities.size() + " new questions successfully";
	}

	private AiProvider getAiProvider() {
		String providerName = aiConfig.getProvider();
		AiProvider provider = aiProviders.get(providerName.toLowerCase());

		if (provider == null) {
			throw new CustomException("Invalid AI Provider", HttpStatus.NOT_FOUND);
		}
		return provider;
	}

	private List<StreakQuestionsDTO> fetchValidQuestions(String prompt, AiProvider provider) {

		for (int attempt = 1; attempt <= 2; attempt++) {
			try {
				logger.debug("Attempting to fetch questions from AI (attempt {}/2)", attempt);

				String response = provider.generate(prompt, aiConfig);

				String cleaned = cleanJson(response);
				return parseQuestions(cleaned);

			} catch (Exception e) {

				logger.warn("Attempt {} failed: {}", attempt, e.getMessage());

				if (attempt == 2) {

					logger.error("AI Question Generation failed after 2 attempts", e);

					emailService.sendAiFailureAlertOnce("StreakService", "fetchValidQuestions", aiConfig.getProvider(),
							aiConfig.getModel(), e.getMessage(), "Daily streak questions could not be generated.",
							"No new questions saved to database.");

					throw new CustomException("AI Question Generation failed", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}

		throw new CustomException("Unexpected failure", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String cleanJson(String response) {
		if (response == null || response.isBlank()) {
			throw new CustomException("Empty AI response", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response = response.trim();
		if (response.startsWith("```")) {
			response = response.replaceAll("```json", "").replaceAll("```", "").trim();
		}
		return response;
	}

	private List<StreakQuestionsDTO> parseQuestions(String json) throws Exception {
		JsonNode node = mapper.readTree(json);
		if (!node.isArray()) {
			throw new CustomException("Invalid JSON format", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return mapper.convertValue(node, new TypeReference<List<StreakQuestionsDTO>>() {
		});
	}

	private List<StreakQuestions> filterNewQuestions(List<StreakQuestionsDTO> dtos) {
		logger.debug("Filtering new questions from {} total", dtos.size());

		List<String> generatedQuestions = dtos.stream().map(StreakQuestionsDTO::getQuestion)
				.collect(Collectors.toList());
		List<StreakQuestions> existing = streakQuestionsRepository.findByQuestionIn(generatedQuestions);
		logger.debug("Found {} existing questions in database", existing.size());

		Set<String> existingSet = existing.stream().map(StreakQuestions::getQuestion).collect(Collectors.toSet());
		List<StreakQuestions> newEntities = dtos.stream().filter(dto -> !existingSet.contains(dto.getQuestion()))
				.map(this::convertToEntity).collect(Collectors.toList());

		if (newEntities.isEmpty()) {
			throw new CustomException("All questions already exist", HttpStatus.CONFLICT);
		}
		return newEntities;
	}

	private StreakQuestions convertToEntity(StreakQuestionsDTO dto) {
		try {
			logger.debug("Converting DTO to entity for question: {}", dto.getQuestion());

			StreakQuestions entity = new StreakQuestions();
			entity.setQuestion(dto.getQuestion());
			entity.setDescription(dto.getDescription());
			entity.setCorrectAnswer(dto.getCorrectAnswer());
			entity.setOptionsJson(mapper.writeValueAsString(dto.getOptions()));
			return entity;
		} catch (Exception e) {
			logger.error("Options serialization failed for question: {}", dto.getQuestion(), e);
			throw new CustomException("Options serialization failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void assignPostDates(List<StreakQuestions> newEntities) {

		LocalDate today = LocalDate.now();
		LocalDate lastDate = streakQuestionsRepository.findMaxPostDate();
		if (lastDate == null || lastDate.isBefore(today)) {
			lastDate = today;
		}

		long countForDate = streakQuestionsRepository.countByPostDate(lastDate);
		LocalDate currentDate = lastDate;
		for (StreakQuestions entity : newEntities) {
			if (countForDate >= DAILY_LIMIT) {
				currentDate = currentDate.plusDays(1);
				countForDate = 0;
			}
			entity.setPostDate(currentDate);
			countForDate++;
		}
	}

	public List<StreakQuestionsDTO> getQuestionsByDate(LocalDate date) {
		logger.debug("Fetching questions for date: {}", date);

		List<StreakQuestionsDTO> dtos = streakQuestionsRepository.findByPostDateOrderByIdAsc(date);
		if (dtos.isEmpty()) {
			throw new CustomException("No questions scheduled for today", HttpStatus.NOT_FOUND);
		}
		return dtos;
	}

	// ========================= STREAK LOGIC =========================

	@Transactional
	public String saveTodayStreak(Long applicantId) {
		logger.info("Saving today's streak for applicant: {}", applicantId);

		LocalDate today = LocalDate.now();
		YearMonth currentMonth = YearMonth.now();
		Applicant applicant = getApplicantOrThrow(applicantId);
		StudentStreaks streak = getOrCreateStreak(applicant);

		if (streak.getAttemptedDates().contains(today)) {
			throw new CustomException("Streak for today already saved", HttpStatus.CONFLICT);
		}

		resetMonthlyRestoreIfNeeded(streak, currentMonth);
		updateStreakLogic(streak, today);
		updateLongestStreak(streak);
		streak.setLastCompletedDate(today);
		streak.getAttemptedDates().add(today);
		logger.debug("Updated streak - Current: {}, Longest: {}", streak.getCurrentStreak(), streak.getLongestStreak());

		studentStreaksRepository.save(streak);
		logger.info("Successfully saved streak for applicant: {}", applicantId);
		return "Streak saved successfully. Current Streak: " + streak.getCurrentStreak() + ", Longest Streak: "
				+ streak.getLongestStreak();
	}

	@Transactional
	public String restoreStreak(Long applicantId) {
		logger.info("Attempting to restore streak for applicant: {}", applicantId);

		LocalDate today = LocalDate.now();
		YearMonth currentMonth = YearMonth.now();
		Applicant applicant = getApplicantOrThrow(applicantId);
		StudentStreaks streak = getStreakOrThrow(applicant);
		resetMonthlyRestoreIfNeeded(streak, currentMonth);

		if (streak.getMonthlyRestoreUsed() >= MAX_MONTHLY_RESTORE) {
			logger.warn("Monthly restore limit exceeded for applicant: {}", applicantId);
			throw new CustomException("Monthly restore limit exceeded", HttpStatus.BAD_REQUEST);
		}
		if (!isRestoreEligible(streak, today)) {
			logger.warn("Restore not eligible for applicant: {}", applicantId);
			throw new CustomException("Restore not allowed", HttpStatus.BAD_REQUEST);
		}
		logger.debug("Restore eligible, updating streak for applicant: {}", applicantId);
		LocalDate missedDate = streak.getLastCompletedDate().plusDays(1);
		streak.setLastCompletedDate(missedDate); // Set to Jan 3
		streak.setMonthlyRestoreUsed(streak.getMonthlyRestoreUsed() + 1);
		studentStreaksRepository.save(streak);
		logger.info("Successfully restored streak for applicant: {}", applicantId);
		return "Streak restored successfully. Current Streak: " + streak.getCurrentStreak() + ", Longest Streak: "
				+ streak.getLongestStreak() + ", Monthly Restores Used: " + streak.getMonthlyRestoreUsed();
	}

	public Map<String, Object> getStreakDetails(Long applicantId) {
		logger.debug("Fetching streak details for applicant: {}", applicantId);

		LocalDate today = LocalDate.now();
		YearMonth currentMonth = YearMonth.now();
		Applicant applicant = getApplicantOrThrow(applicantId);
		StudentStreaks streak = getStreakOrThrow(applicant);
		resetMonthlyRestoreIfNeeded(streak, currentMonth);

		boolean restoreEligible = isRestoreEligible(streak, today);
		int remainingRestores = MAX_MONTHLY_RESTORE - streak.getMonthlyRestoreUsed();
		boolean restoreAvailable = restoreEligible && remainingRestores > 0;
		logger.debug("Streak details - Current: {}, Longest: {}, RestoreAvailable: {}, RemainingRestores: {}",
				streak.getCurrentStreak(), streak.getLongestStreak(), restoreAvailable, remainingRestores);

		Map<String, Object> result = new java.util.HashMap<>();
		result.put("currentStreak", streak.getCurrentStreak());
		result.put("longestStreak", streak.getLongestStreak());
		result.put("restoreAvailable", restoreAvailable);
		result.put("monthlyRestoreRemaining", remainingRestores);
		result.put("attemptedToday", LocalDate.now().equals(streak.getLastCompletedDate()));
		return result;
	}

	// HELPER METHODS

	private Applicant getApplicantOrThrow(Long applicantId) {
		logger.debug("Retrieving applicant with ID: {}", applicantId);
		return applicantRepository.findById(applicantId).orElseThrow(() -> {
			logger.error("Applicant not found with ID: {}", applicantId);
			return new CustomException("Applicant not found", HttpStatus.NOT_FOUND);
		});
	}

	private StudentStreaks getStreakOrThrow(Applicant applicant) {
		logger.debug("Retrieving streak for applicant ID: {}", applicant.getId());
		return studentStreaksRepository.findByApplicant(applicant).orElseThrow(() -> {
			logger.error("Streak not found for applicant ID: {}", applicant.getId());
			return new CustomException("Streak not found", HttpStatus.NOT_FOUND);
		});
	}

	private StudentStreaks getOrCreateStreak(Applicant applicant) {
		return studentStreaksRepository.findByApplicant(applicant).orElseGet(() -> new StudentStreaks(applicant));
	}

	private void resetMonthlyRestoreIfNeeded(StudentStreaks streak, YearMonth currentMonth) {

		if (streak.getRestoreMonth() == null || !currentMonth.equals(streak.getRestoreMonth())) {

			streak.setMonthlyRestoreUsed(0);
			streak.setRestoreMonth(currentMonth);
		}
	}

	private void updateStreakLogic(StudentStreaks streak, LocalDate today) {

		if (streak.getLastCompletedDate() == null) {
			streak.setCurrentStreak(1);
		} else if (streak.getLastCompletedDate().plusDays(1).equals(today)) {
			streak.setCurrentStreak(streak.getCurrentStreak() + 1);
		} else {
			streak.setCurrentStreak(1);
		}
	}

	private void updateLongestStreak(StudentStreaks streak) {
		if (streak.getCurrentStreak() > streak.getLongestStreak()) {
			streak.setLongestStreak(streak.getCurrentStreak());
		}
	}

	private boolean isRestoreEligible(StudentStreaks streak, LocalDate today) {
		if (streak.getAttemptedDates().contains(today)) {
			return false;
		}

		LocalDate lastDate = streak.getLastCompletedDate();
		if (lastDate == null) {
			return false;
		}

		long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastDate, today);
		return daysBetween == 2;
	}

	public List<StreakQuestionsDTO> getAllAttemptedQuestions(Long applicantId) {
		logger.info("Fetching all attempted questions for applicant: {}", applicantId);

		Applicant applicant = getApplicantOrThrow(applicantId);
		StudentStreaks streak = getStreakOrThrow(applicant);
		List<LocalDate> attemptedDates = streak.getAttemptedDates();

		if (attemptedDates == null || attemptedDates.isEmpty()) {
			logger.warn("No attempted questions found for applicant: {}", applicantId);
			throw new CustomException("No attempted questions found", HttpStatus.NOT_FOUND);
		}
		logger.debug("Found {} attempted dates for applicant: {}", attemptedDates.size(), applicantId);

		// Pass list directly
		List<StreakQuestions> questions = streakQuestionsRepository.findByPostDateInOrderByPostDateDesc(attemptedDates);
		if (questions.isEmpty()) {
			logger.warn("No questions found for attempted dates for applicant: {}", applicantId);
			throw new CustomException("No questions found", HttpStatus.NOT_FOUND);
		}
		logger.info("Retrieved {} attempted questions for applicant: {}", questions.size(), applicantId);
		return questions.stream().map(q -> new StreakQuestionsDTO(q.getQuestion(), q.getDescription(),
				q.getCorrectAnswer(), q.getOptionsJson(), q.getPostDate(), mapper)).collect(Collectors.toList());
	}

	public void sendDailyStreakReminders(String type) {
		logger.info("Starting to send daily streak reminders of type: {}", type);

		List<StudentStreaks> missedList = studentStreaksRepository.findApplicantsWhoMissedToday();
		logger.info("Found {} applicants who missed today's streak", missedList.size());

		for (StudentStreaks streak : missedList) {
			Applicant applicant = streak.getApplicant();
			if (applicant != null) {
				logger.debug("Sending streak reminder to applicant: {} ({})", applicant.getId(), applicant.getEmail());
				emailService.sendStreakReminderEmail(applicant.getEmail(), "https://yourdomain.com/daily-test", type);
			} else {
				logger.warn("Applicant not found for streak ID: {}", streak.getApplicant().getId());
			}
		}
		logger.info("Completed sending daily streak reminders");
	}

	public List<LocalDate> getAttemptedDates(Long applicantId) {
 
	    logger.info("Fetching attempted dates for applicant: {}", applicantId);
 
	    if (applicantId == null) {
	        logger.error("Applicant ID cannot be null");
	        throw new CustomException("Applicant ID cannot be null", HttpStatus.BAD_REQUEST);
	    }
 
	    String json = studentStreaksRepository.findAttemptedDatesByApplicantId(applicantId);
 
	    if (json == null || json.isBlank()) {
	        logger.warn("No attempted dates found for applicant: {}", applicantId);
	        throw new CustomException("No attempted dates found", HttpStatus.NOT_FOUND);
	    }
 
	    try {
	        List<LocalDate> dates = mapper.readValue(
	                json,
	                new TypeReference<List<LocalDate>>() {}
	        );
 
	        logger.info("Successfully fetched {} attempted dates for applicant {}", dates.size(), applicantId);
 
	        return dates;
 
	    } catch (Exception e) {
	        logger.error("Failed to parse attempted dates JSON for applicant {}", applicantId, e);
	        throw new CustomException("Failed to process attempted dates", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
