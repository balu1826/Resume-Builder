package com.talentstream.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ResumeAIEnhancerService {

    @Autowired
    private RestTemplate restTemplate;

    private final String groqApiKey = "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";
    private final String groqApiUrl = "https://api.groq.com/openai/v1/chat/completions";
    private final String groqModel = "llama-3.1-8b-instant";

    /**
     * Enhances resume summary into 3–4 line ATS-friendly professional format.
     */
    public String enhanceSummary(String summary,String role,String jd) {
        String prompt ;
        if(jd.trim().length()>1) {
        	  prompt =
        	            "Enhance the following resume summary to align with the provided Job Description.\n" +
        	            "Use relevant skills, keywords, and responsibilities from the JD to make it ATS-friendly and professional.\n" +
        	            "Output must be 3-4 lines, concise, and factual.\n" +
        	            "Do NOT add markdown, bullets, JSON, or headings.\n\n" +
        	            "SUMMARY:\n" + summary + "\n\n" +
        	            "JOB DESCRIPTION:\n" + jd + "\n\n" +
        	            "Return only the enhanced summary text.";
        }
        else {
        	prompt =
                    "Enhance the following resume summary to suit the role of '" + role + "'.\n" +
                            "Use relevant role keywords to improve ATS score.\n" +
                            "Output must be 3-4 lines, concise, professional, and factual.\n" +
                            "Do NOT add markdown, bullets, JSON, or headings.\n\n" +
                            "SUMMARY:\n" + summary + "\n\n" +
                            "Return only the enhanced summary text.";
        }
           
        return callGroq(prompt);
    }

    /**
     * Enhances project title + description into strong bullet-point style.
     */
    public String enhanceProject(String title, String description) {
        String prompt =
            "Rewrite the following project details into 2–4 professional bullet points using strong action verbs.\n" +
            "Highlight responsibilities, impact, technologies, and outcomes.\n" +
            "Do NOT add markdown formatting, code blocks, or JSON. Just bullet points separated by newline.\n\n" +
            "PROJECT TITLE: " + title + "\n" +
            "DESCRIPTION: " + description;

        return callGroq(prompt);
    }

    private String callGroq(String userContent) {

        // Prepare messages array
        JsonArray messages = new JsonArray();

        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("content", "You are an expert ATS resume and project enhancement assistant.");
        messages.add(system);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", userContent);
        messages.add(user);

        // Request body
        JsonObject body = new JsonObject();
        body.addProperty("model", groqModel);
        body.add("messages", messages);
        body.addProperty("temperature", 0.3);
        body.addProperty("max_tokens", 400);

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        // Send request
        ResponseEntity<String> response = restTemplate.exchange(
            groqApiUrl, HttpMethod.POST, entity, String.class
        );

        // Parse response
        JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
        String content = json.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString()
                .trim();

        return content;
    }
}
