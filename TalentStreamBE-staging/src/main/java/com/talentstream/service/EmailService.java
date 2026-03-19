package com.talentstream.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.talentstream.AwsSecretsManagerUtil;

@Service
public class EmailService {

	@Autowired
	private AwsSecretsManagerUtil secretsManagerUtil;

	private String getSecret() {
		return secretsManagerUtil.getSecret();
	}

	private JavaMailSenderImpl getJavaMailSender() {
		String secret = getSecret();
		JSONObject jsonObject = new JSONObject(secret);
		String userName = jsonObject.getString("AWS_EMAIL_USERNAME");
		String passWord = jsonObject.getString("AWS_EMAIL_PASSWORD");

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("email-smtp.ap-south-1.amazonaws.com");
		mailSender.setPort(587);

		mailSender.setUsername(userName);
		mailSender.setPassword(passWord);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

	// Common reusable method (only duplication removed)
	private void sendEmail(String to, String subject, String content)
			throws MessagingException, UnsupportedEncodingException {

		JavaMailSenderImpl mailSender = getJavaMailSender();
		javax.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setFrom(new InternetAddress("no-reply@bitlabs.in", "bitLabs "));
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content);

		mailSender.send(mimeMessage);
	}

	// OTP EMAIL
	public void sendOtpEmail(String to, String otp) {
		try {
			String content = "Dear bitLabs User,\n\n" + "Your OTP is: " + otp + "\n\n"
					+ "We received a request to verify your identity for bitLabs. To complete the verification process, please use the above One-Time Password (OTP).\n\n"
					+ "This OTP is valid for the next 1 minute. For your security, please do not share this OTP with anyone.\n\n"
					+ "If you did not request this verification, please ignore this email.\n\n"
					+ "Thank you for using bitLabs!\n\n" + "Best regards,\n" + "The bitLabs Team\n\n"
					+ "This is an auto-generated email. Please do not reply.";

			sendEmail(to, "OTP verification for bitLabs ", content);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// MENTOR CONNECT
	public void sendMentorConnectRegistrationEmailToApplicant(String subject, String message, String email) {
		try {
			sendEmail(email, subject, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Async
	public void sendStreakReminderEmail(String toEmail, String testLink, String type) {
		try {

			String subject;
			String content;

			if ("MORNING".equalsIgnoreCase(type)) {

				subject = "Start Your Day Strong 💪 Maintain Your Streak!";

				content = "Dear Streak Star,\n\n" + "A new day means a new opportunity to grow!\n\n"
						+ "Don’t forget to attempt today’s test and keep your streak alive.\n\n"
						+ "Consistency builds success. Stay ahead of the competition.\n\n"
						+ "Click below to complete your test now:\n" + testLink + "\n\n" + "Best regards,\n"
						+ "The bitLabs Team\n\n" + "This is an auto-generated email. Please do not reply.";

			} else { // EVENING

				subject = "Last Chance to Save Your Streak 🔥";

				content = "Dear Streak Star,\n\n" + "You haven’t attempted today’s test yet.\n\n"
						+ "Don’t let your streak break now!\n\n"
						+ "It only takes a few minutes to complete today’s challenge.\n\n"
						+ "Complete your test before the day ends:\n" + testLink + "\n\n" + "Best regards,\n"
						+ "The bitLabs Team\n\n" + "This is an auto-generated email. Please do not reply.";
			}

			sendEmail(toEmail, subject, content);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Prevent multiple emails per execution
	private volatile boolean aiFailureMailSent = false;

	public void resetAiFailureFlag() {
		aiFailureMailSent = false;
	}

	@Async
	public void sendAiFailureAlertOnce(String serviceName, String methodName, String provider, String model,
			String errorMessage, String impact, String fallbackBehavior) {

		if (aiFailureMailSent) {
			return;
		}

		synchronized (this) {
			if (aiFailureMailSent) {
				return;
			}

			aiFailureMailSent = true;
		}

		try {

			String subject = "🚨 CRITICAL ALERT: AI Service Failure";

			String content = "AI SERVICE FAILURE ALERT\n\n" + "Service Name      : " + serviceName + "\n"
					+ "Method Name       : " + methodName + "\n" + "AI Provider       : " + provider + "\n"
					+ (model != null ? "Model Used        : " + model + "\n" : "") + "\nError Message     : "
					+ errorMessage + "\n\n" + "Impact            : " + impact + "\n\n" + "System Behavior   : "
					+ fallbackBehavior + "\n\n" + "Timestamp         : " + java.time.LocalDateTime.now() + "\n\n"
					+ "This is an automated alert.";

			String[] teamEmails = { "vineetha.margana@tekworks.in", "shanmukhaganesh2002@gmail.com",
					"balubattula1826@gmail.com", "nagulmeera.shaik@tekworks.in", "karunakar.eede@tekworks.in" };

			for (String email : teamEmails) {
				sendEmail(email, subject, content);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
