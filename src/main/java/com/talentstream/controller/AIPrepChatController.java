package com.talentstream.controller;

import com.talentstream.dto.AIPrepChatDTO;
import com.talentstream.dto.ChatTitleDTO;
import com.talentstream.service.AIPrepChatService;
import com.talentstream.repository.AIPrepChatRepository;
import com.talentstream.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/aiPrepChat")
public class AIPrepChatController {

    @Autowired
    private AIPrepChatService aiPrepChatService;

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private AIPrepChatRepository aiPrepChatRepository;

    @PostMapping("/saveChat")
    public ResponseEntity<?> saveChat(@RequestBody AIPrepChatDTO chatDTO) {
        try {
            if (chatDTO == null) return ResponseEntity.badRequest().body("Request body cannot be null");
            if (chatDTO.getApplicantId() == null) return ResponseEntity.badRequest().body("applicantId is required");
            if (!registerRepository.existsById(chatDTO.getApplicantId()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
            if (chatDTO.getTitle() == null || chatDTO.getTitle().trim().isEmpty())
                return ResponseEntity.badRequest().body("title is required");
            if (chatDTO.getSavedChat() == null || chatDTO.getSavedChat().trim().isEmpty())
                return ResponseEntity.badRequest().body("chat is required");

            AIPrepChatDTO savedChat = aiPrepChatService.saveChat(chatDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedChat);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/getAllChatTitles/{applicantId}")
    public ResponseEntity<?> getChatTitles(@PathVariable Long applicantId) {
        try {
            if (applicantId == null)
                return ResponseEntity.badRequest().body("applicantId is required");
            if (!registerRepository.existsById(applicantId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");

            List<ChatTitleDTO> titles = aiPrepChatService.getChatTitlesByApplicantId(applicantId);

            return titles == null || titles.isEmpty()
                    ? ResponseEntity.ok("There are no saved chats")
                    : ResponseEntity.ok(titles);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch chat titles");
        }
    }


    @GetMapping("/{chatId}/getChatDetailsById/{applicantId}")
    public ResponseEntity<?> getChatById(@PathVariable Long chatId, @PathVariable Long applicantId) {
        try {
            if (applicantId == null) return ResponseEntity.badRequest().body("applicantId is required");
            if (chatId == null) return ResponseEntity.badRequest().body("chatId is required");
            if (!registerRepository.existsById(applicantId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
            if (!aiPrepChatRepository.existsById(chatId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat is not present");

            Optional<AIPrepChatDTO> chat = aiPrepChatService.getChatById(chatId, applicantId);
            return chat.isPresent()
                    ? ResponseEntity.ok(chat.get())
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chat does not belong to the applicant");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch chat");
        }
    }


    @PutMapping("/{chatId}/updateChatDetails/{applicantId}")
    public ResponseEntity<?> updateChat(@PathVariable Long chatId, @PathVariable Long applicantId,
                                        @RequestBody AIPrepChatDTO chatDTO) {
        try {
            if (applicantId == null) return ResponseEntity.badRequest().body("applicantId is required");
            if (chatId == null) return ResponseEntity.badRequest().body("chatId is required");
            if (!registerRepository.existsById(applicantId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
            if (!aiPrepChatRepository.existsById(chatId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat is not present");

            Optional<AIPrepChatDTO> updatedChat = aiPrepChatService.updateChat(chatId, applicantId, chatDTO);
            return updatedChat.isPresent()
                    ? ResponseEntity.ok(updatedChat.get())
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chat does not belong to the applicant");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update chat");
        }
    }


    @DeleteMapping("/{chatId}/deleteChat/{applicantId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId, @PathVariable Long applicantId) {
        try {
            if (chatId == null || applicantId == null)
                return ResponseEntity.badRequest().body("chatId and applicantId are required");
            if (!registerRepository.existsById(applicantId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
            if (!aiPrepChatRepository.existsById(chatId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat is not present");

            return aiPrepChatService.deleteChat(chatId, applicantId)
                    ? ResponseEntity.ok("Chat deleted successfully")
                    : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chat does not belong to the applicant");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting chat: " + e.getMessage());
        }
    }


    @GetMapping("/getAllChats/{applicantId}")
    public ResponseEntity<?> getAllChats(@PathVariable Long applicantId) {
        try {
            if (applicantId == null)
                return ResponseEntity.badRequest().body("applicantId is required");
            if (!registerRepository.existsById(applicantId))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");

            return ResponseEntity.ok(aiPrepChatService.getAllChatsByApplicantId(applicantId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch chats");
        }
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(
            @RequestParam(required = false) Long chatId,
            @RequestParam Long applicantId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String message = body.get("message");

            if (message == null || message.trim().isEmpty())
                return ResponseEntity.badRequest().body("Message cannot be empty");

            // save user message
            AIPrepChatDTO chat = aiPrepChatService.saveUserMessage(chatId, applicantId, message);

            // Get AI response (will integrate Groq later)
            String aiReply = "AI reply placeholder";

            // save bot reply
            aiPrepChatService.saveBotMessage(chat.getChatId(), applicantId, aiReply);

            Map<String, Object> response = new HashMap<>();
            response.put("chatId", chat.getChatId());
            response.put("reply", aiReply);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{chatId}/history/{applicantId}")
    public ResponseEntity<?> getHistory(@PathVariable Long chatId, @PathVariable Long applicantId) {
        try {
            return ResponseEntity.ok(aiPrepChatService.getChatHistory(chatId, applicantId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching history: " + e.getMessage());
        }
    }

    @GetMapping("/{chatId}/lastQuestion/{applicantId}")
    public ResponseEntity<?> getLastQuestion(@PathVariable Long chatId, @PathVariable Long applicantId) {
        try {
            return ResponseEntity.ok(aiPrepChatService.getLastUserQuestion(chatId, applicantId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching last question: " + e.getMessage());
        }
    }
}
