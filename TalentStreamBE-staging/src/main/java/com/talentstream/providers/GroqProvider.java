package com.talentstream.providers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.config.AiConfig;
import com.talentstream.exception.CustomException;

@Component("groq")
public class GroqProvider implements AiProvider {

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public String generate(String prompt, AiConfig config) {

		if (config == null) {
			throw new CustomException("AI configuration is missing", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// ✅ Store in local variable (fixes null safety warning)
		String apiKey = config.getApiKey();
		String baseUrl = config.getBaseUrl();
		String model = config.getModel();

		if (apiKey == null || apiKey.isBlank()) {
			throw new CustomException("Groq API key is missing", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (baseUrl == null || baseUrl.isBlank()) {
			throw new CustomException("Groq Base URL is missing", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (model == null || model.isBlank()) {
			throw new CustomException("Groq model is missing", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {

			String url = baseUrl;

			// Create message
			Map<String, Object> message = new HashMap<>();
			message.put("role", "user");
			message.put("content", prompt);

			// Create request body
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", model);
			requestBody.put("messages", Collections.singletonList(message));

			// Headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(apiKey);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {

				throw new CustomException("Invalid response from Groq API", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			JsonNode root = mapper.readTree(response.getBody());
			JsonNode choices = root.path("choices");

			if (choices.isArray() && choices.size() > 0) {
				return choices.get(0).path("message").path("content").asText();
			}

			throw new CustomException("No content returned from Groq API", HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (CustomException ex) {
			throw ex; 
		} catch (Exception e) {
			throw new CustomException("Groq API Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}