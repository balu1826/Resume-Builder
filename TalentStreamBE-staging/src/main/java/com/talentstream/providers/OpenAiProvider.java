package com.talentstream.providers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.config.AiConfig;
import com.talentstream.exception.CustomException;

@Component("openai")
public class OpenAiProvider implements AiProvider {

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public String generate(String prompt, AiConfig config) {

		try {

			String url = config.getBaseUrl();

			// Create message object
			Map<String, String> message = new HashMap<>();
			message.put("role", "user");
			message.put("content", prompt);

			// Create request body
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", config.getModel());
			requestBody.put("messages", Arrays.asList(message));
			requestBody.put("temperature", 0.7);

			// Headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(config.getApiKey());

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

				JsonNode root = mapper.readTree(response.getBody());

				return root.path("choices").get(0).path("message").path("content").asText();
			}

		} catch (Exception e) {
			throw new CustomException("OpenAI API Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return "Unable to generate content";
	}
}
