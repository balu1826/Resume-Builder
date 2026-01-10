package com.talentstream.service;
 
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import com.talentstream.dto.AIPrepChatDTO;

import com.talentstream.dto.AIInterviewPrepBotDTO;

import com.talentstream.dto.ChatTitleDTO;

import com.talentstream.entity.AIPrepChat;

import com.talentstream.repository.AIPrepChatRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
 
import java.lang.reflect.Type;

import java.time.LocalDateTime;

import java.util.*;

import java.util.stream.Collectors;
 
@Service

public class AIPrepChatService {
 
    @Autowired

    private AIPrepChatRepository aiPrepChatRepository;
 
    private final Gson gson = new Gson();

    private final Type chatListType = new TypeToken<List<Map<String, String>>>(){}.getType();

 
    @Transactional

    public AIPrepChatDTO saveChat(AIPrepChatDTO chatDTO) {
 
        AIPrepChat chat = new AIPrepChat();

        chat.setApplicantId(chatDTO.getApplicantId());

        chat.setTitle(chatDTO.getTitle());

        chat.setSavedChat(chatDTO.getSavedChat());

        chat.setCreatedAt(LocalDateTime.now());
 
        AIPrepChat savedChat = aiPrepChatRepository.save(chat);
 
        return convertToDTO(savedChat);

    }
 
    public Optional<AIPrepChatDTO> getChatById(Long chatId, Long applicantId) {
 
        return aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId)

                .map(this::convertToDTO);

    }
 
    @Transactional

    public Optional<AIPrepChatDTO> updateChat(Long chatId, Long applicantId, AIPrepChatDTO chatDTO) {
 
        Optional<AIPrepChat> existing = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId);
 
        if (existing.isPresent()) {

            AIPrepChat chat = existing.get();
 
            if (chatDTO.getTitle() != null)

                chat.setTitle(chatDTO.getTitle());
 
            if (chatDTO.getSavedChat() != null)

                chat.setSavedChat(chatDTO.getSavedChat());
 
            chat.setUpdatedAt(LocalDateTime.now());

            aiPrepChatRepository.save(chat);
 
            return Optional.of(convertToDTO(chat));

        }

        return Optional.empty();

    }
 
    @Transactional

    public AIPrepChatDTO saveUserMessage(Long chatId, Long applicantId, String userMessage) {
 
        AIPrepChat chat;
 
        if (chatId != null) {

            chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId)

                    .orElseThrow(() -> new RuntimeException("Chat not found"));

        } else {

            chat = new AIPrepChat(applicantId, "New Chat", "[]");

            chat = aiPrepChatRepository.save(chat);

        }
 
        appendMessage(chat, "user", userMessage);
 
        if (chat.getTitle().equals("New Chat")) {

            chat.setTitle(generateTitle(userMessage));

        }
 
        aiPrepChatRepository.save(chat);

        return convertToDTO(chat);

    }
 
 
    @Transactional

    public void saveBotMessage(Long chatId, Long applicantId, String botMessage) {
 
        AIPrepChat chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId)

                .orElseThrow(() -> new RuntimeException("Chat not found"));
 
        appendMessage(chat, "assistant", botMessage);

        aiPrepChatRepository.save(chat);

    }
 
 
    private void appendMessage(AIPrepChat chat, String role, String content) {
 
        List<Map<String, String>> history = getHistory(chat);
 
        Map<String, String> entry = new LinkedHashMap<>();

        entry.put("role", role);

        entry.put("message", content);

        entry.put("time", LocalDateTime.now().toString());
 
        history.add(entry);
 
        chat.setSavedChat(gson.toJson(history));

    }
 
 
    private List<Map<String, String>> getHistory(AIPrepChat chat) {

        if (chat.getSavedChat() == null || chat.getSavedChat().isEmpty())

            return new ArrayList<>();
 
        return gson.fromJson(chat.getSavedChat(), chatListType);

    }
 
 
    public String getLastUserQuestion(Long chatId, Long applicantId) {
 
        AIPrepChat chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId)

                .orElseThrow(() -> new RuntimeException("Chat not found"));
 
        List<Map<String, String>> history = getHistory(chat);
 
        for (int i = history.size() - 1; i >= 0; i--) {

            if ("user".equals(history.get(i).get("role"))) {

                return history.get(i).get("message");

            }

        }

        return null;

    }
 
 
    public List<Map<String, String>> getChatHistory(Long chatId, Long applicantId) {
 
        AIPrepChat chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId)

                .orElseThrow(() -> new RuntimeException("Chat not found"));
 
        return getHistory(chat);

    }
 
 
    private String generateTitle(String text) {

        return text.length() > 30 ? text.substring(0, 30) + "..." : text;

    }
 
 
    public List<ChatTitleDTO> getChatTitlesByApplicantId(Long applicantId) {

        return aiPrepChatRepository.findChatTitlesByApplicantId(applicantId);

    }
 
 
    @Transactional

    public boolean deleteChat(Long chatId, Long applicantId) {
 
        Optional<AIPrepChat> chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId);
 
        if (chat.isPresent()) {

            aiPrepChatRepository.deleteByChatIdAndApplicantId(chatId, applicantId);

            return true;

        }
 
        return false;

    }
 
 
    public List<AIPrepChatDTO> getAllChatsByApplicantId(Long applicantId) {
 
        return aiPrepChatRepository.findByApplicantIdOrderByCreatedAtDesc(applicantId)

                .stream()

                .map(this::convertToDTO)

                .collect(Collectors.toList());

    }
 
 
    private AIPrepChatDTO convertToDTO(AIPrepChat chat) {

        return new AIPrepChatDTO(

                chat.getChatId(),

                chat.getApplicantId(),

                chat.getTitle(),

                chat.getSavedChat(),

                chat.getCreatedAt(),

                chat.getUpdatedAt()

        );

    }

    public Map<String, Object> getApplicantProfile(AIInterviewPrepBotDTO dto) {
 
        Map<String, Object> profile = new LinkedHashMap<>();
 
        profile.put("basicDetails", dto.getBasicDetails());

        profile.put("skillsRequired", dto.getSkillsRequired());

        profile.put("experience", dto.getExperience());

        profile.put("experienceDetails", dto.getExperienceDetails());

        profile.put("qualification", dto.getQualification());

        profile.put("specialization", dto.getSpecialization());

        profile.put("preferredJobLocations", dto.getPreferredJobLocations());

        profile.put("roles", dto.getRoles());
 
        return profile;

    }

}

 