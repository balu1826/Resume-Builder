package com.talentstream.service;
import com.google.gson.JsonArray;
 
import com.google.gson.JsonObject;
 
import com.google.gson.JsonParser;
 
import com.talentstream.dto.AIInterviewPrepBotDTO;
import com.talentstream.dto.AIPrepChatDTO;
import com.talentstream.entity.ApplicantProfile;
 
import com.talentstream.entity.ApplicantSkills;
 
import com.talentstream.repository.ApplicantProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
 
import org.springframework.http.*;
 
import org.springframework.stereotype.Service;
 
import org.springframework.web.client.RestClientException;
 
import org.springframework.web.client.RestTemplate;
import java.util.*;
 
import java.util.stream.Collectors;
@Service
 
public class AIInterviewPrepBotService {
    @Autowired
 
    private RestTemplate restTemplate;
    @Autowired
 
    private AIPrepChatService aiPrepChatService;
    @Autowired
 
    private ApplicantProfileRepository applicantProfileRepository;
    private String groqApiKey = "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";
 
    private String groqApiUrl = "https://api.groq.com/openai/v1/chat/completions";
 
    private String groqModel = "llama-3.1-8b-instant";
    public JsonObject answer(AIInterviewPrepBotDTO dto) {
        try {
 
            Long chatId = dto.getChatId();
 
            Long applicantId = dto.getApplicantId();
 
            String userMessage = dto.getRequest();

 
            AIPrepChatDTO chatDTO = aiPrepChatService.saveUserMessage(chatId, applicantId, userMessage);
 
            chatId = chatDTO.getChatId();

 
            List<Map<String, String>> history = aiPrepChatService.getChatHistory(chatId, applicantId);

 
            List<ApplicantProfile> profiles =
 
                    applicantProfileRepository.findByApplicantIdIn(Collections.singletonList(applicantId));
            String extractedSkills = "No skills found";
            if (!profiles.isEmpty()) {
 
                Set<ApplicantSkills> skills = profiles.get(0).getSkillsRequired();
                if (skills != null && !skills.isEmpty()) {
 
                    extractedSkills = skills.stream()
 
                            .map(ApplicantSkills::getSkillName)
 
                            .collect(Collectors.joining(", "));
 
                }
 
            }

 
            JsonArray messages = new JsonArray();
            JsonObject systemMsg = new JsonObject();
 
            systemMsg.addProperty("role", "system");
 
            systemMsg.addProperty(
 
            	    "content",
 
            	    "You are an AI assistance bot called Ask Newton.\n" +
 
            	    "The applicant has the following skills: " + extractedSkills + ".\n" +
 
            	    "Use these skills while answering any skill-related question.\n" +
 
            	    "\n" +
 
            	    "⚠️ IMPORTANT — RESPONSE FORMAT RULES ⚠️\n" +
 
            	    "You must ALWAYS respond ONLY in valid JSON format.\n" +
 
            	    "Never send plain text outside JSON. Never wrap JSON in backticks.\n" +
 
            	    "\n" +
 
            	    "Your response MUST follow EXACTLY this structure:\n" +
 
            	    "{\n" +
 
            	    "   \"response\": \"your main answer here\",\n" +
 
            	    "   \"followup\": [\"response 1\", \"response 2\", \"response 3\"]\n" +
 
            	    "}\n" +
 
            	    "\n" +
 
            	    "Rules:\n" +
 
            	    "1. The value of 'response' MUST be a STRING.\n" +
 
            	    "2. The value of 'followup' MUST be an ARRAY of 2–4 strings.\n" +
 
            	    "3. Do NOT escape the word response or followup and response or follow up has to be like that user is asking bot ,not like bot  asking the user.\n" +
 
            	    "4. Do NOT create nested JSON like {\"response\": { ... }}.\n" +
 
            	    "5. Do NOT include markdown, backticks, or code fences.\n" +
 
            	    "6. If code is included, put it inside the string of 'response'. Example:\n" +
 
            	    "   { \"response\": \"Here is code:\\npublic class A { }\", \"followup\": [\"...\"] }\n" +
 
            	    "7. Never add other fields except 'response' and 'followup'.\n" +
            	    
            	    "8. FOLLOWUP RULE (STRICT):\n"
            	    + "- The \"followup\" array MUST contain only statements written as if the USER is speaking to the bot.\n"
            	    + "- Followups must represent possible next messages the user would send.\n"
            	    + "- Do NOT ask the user anything.\n"
            	    + "- Do NOT use questions, question marks, or assistant language.\n"
            	    + "- Use first-person intent-driven phrasing.\n"
            	    + "\n"
            	    + "Correct examples:\n"
            	    + "- \"want to change the topic\"\n"
            	    + "- \"want to learn this in depth\"\n"
            	    + "- \"want to practice advanced interview questions\"\n"
            	    + "- \"want to move to system design\"\n"
            	    + "\n"
            	    + "Incorrect examples:\n"
            	    + "- \"Do you want to change the topic?\"\n"
            	    + "- \"Would you like to learn more?\"\n"
            	    + "- \"Ask another question\"\n"
            	    + "- \"Choose an option\"\n"
            	    + "  "+
 
            	    "If the user's question cannot be answered in JSON, still return:\n" +
 
            	    "{ \"response\": \"I cannot answer that.\", \"followup\": [\"Ask another question?\"] }"
 
            	);
 
            	 
            messages.add(systemMsg);

 
            for (Map<String, String> entry : history) {
 
                JsonObject msg = new JsonObject();
 
                msg.addProperty("role", entry.get("role"));
 
                msg.addProperty("content", entry.get("message"));
 
                messages.add(msg);
 
            }

 
            JsonObject requestBody = new JsonObject();
 
            requestBody.addProperty("model", groqModel);
 
            requestBody.add("messages", messages);
 
            requestBody.addProperty("temperature", 0.5);
 
            requestBody.addProperty("max_tokens", 800);
            HttpHeaders headers = new HttpHeaders();
 
            headers.setContentType(MediaType.APPLICATION_JSON);
 
            headers.set("Authorization", "Bearer " + groqApiKey);
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

 
            ResponseEntity<String> response =
 
                    restTemplate.exchange(groqApiUrl, HttpMethod.POST, entity, String.class);
            String responseBody = response.getBody();

 
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
 
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices != null && choices.size() > 0) {
 
                JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
 
                String content = message.get("content").getAsString();
                JsonObject finalResponse;
                try {
 
                    finalResponse = JsonParser.parseString(content).getAsJsonObject();
 
                } catch (Exception ex) {
 
                    finalResponse = new JsonObject();
 
                    finalResponse.addProperty("response", content);
 
                }
                if (!finalResponse.has("response")) {
 
                    finalResponse.addProperty("response", content);
 
                }

 
                aiPrepChatService.saveBotMessage(chatId, applicantId,
 
                        finalResponse.get("response").getAsString()
 
                );
                finalResponse.addProperty("chatId", chatId);
 
                return finalResponse;
 
            }
            // Fallback
 
            JsonObject fallback = new JsonObject();
 
            fallback.addProperty("response", "No valid response received.");
 
            fallback.addProperty("chatId", chatId);
 
            return fallback;
        } catch (RestClientException e) {
 
            JsonObject error = new JsonObject();
 
            error.addProperty("response", "Connection error: " + e.getMessage());
 
            return error;
        } catch (Exception e) {
 
            JsonObject error = new JsonObject();
 
            error.addProperty("response", "Unexpected error: " + e.getMessage());
 
            return error;
 
        }
 
    }
 
}