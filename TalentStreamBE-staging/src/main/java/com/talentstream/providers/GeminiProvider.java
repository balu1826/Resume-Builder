package com.talentstream.providers;

import java.util.Arrays;
import java.util.Collections;
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

@Component("gemini")
public class GeminiProvider implements AiProvider {

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public String generate(String prompt, AiConfig config) {

		try {

			String url = config.getBaseUrl() + "/models/" + config.getModel() + ":generateContent?key="
					+ config.getApiKey();

			Map<String, Object> part = Collections.singletonMap("text", prompt);
			Map<String, Object> contents = Collections.singletonMap("parts", Arrays.asList(part));
			Map<String, Object> requestBody = Collections.singletonMap("contents", Arrays.asList(contents));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

				JsonNode root = mapper.readTree(response.getBody());

				return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
			}

		} catch (Exception e) {
			throw new CustomException("Gemini API Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return "Unable to generate content";
	}

}