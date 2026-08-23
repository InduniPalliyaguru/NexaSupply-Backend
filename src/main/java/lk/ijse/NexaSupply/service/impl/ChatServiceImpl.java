package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.chatbot.ChatRequestDTO;
import lk.ijse.NexaSupply.dto.chatbot.ChatResponseDTO;
import lk.ijse.NexaSupply.entity.ChatLog;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.repository.ChatLogRepository;
import lk.ijse.NexaSupply.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatLogRepository chatLogRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String baseUrl;

    private static final String SYSTEM_PROMPT =
            "You are NexaSupply AI Assistant. You MUST ONLY answer questions strictly related to " +
                    "NexaSupply inventory management, products, orders, restocking, suppliers, and system features. " +
                    "If the user asks about countries, sports, general knowledge, movies, or anything unrelated to NexaSupply, " +
                    "decline politely by saying: 'I am NexaSupply AI Assistant. I can only answer questions related to NexaSupply inventory, orders, and services.'";

    @Override
    public ChatResponseDTO processChat(ChatRequestDTO requestDTO, String userEmail, Role userRole) {
        log.info("Execute processChat method");

        String userPrompt = requestDTO.getMessage();
        String apiUrl = baseUrl + "?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", SYSTEM_PROMPT))
        );

        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", "User Role: " + userRole + "\nQuestion: " + userPrompt))
        );

        Map<String, Object> requestBody = Map.of(
                "system_instruction", systemInstruction,
                "contents", List.of(userContent)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String aiReply = "";

        try {

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    List parts = (List) content.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    aiReply = (String) firstPart.get("text");
                }
            }
        } catch (Exception e) {
            log.error("Exception in processChat method", e);
            aiReply = "Sorry, I am having trouble connecting to AI services right now. Please try again later.";
        }

        ChatLog chatLog = new ChatLog();
        chatLog.setUserEmail(userEmail);
        chatLog.setUserRole(userRole);
        chatLog.setUserPrompt(userPrompt);
        chatLog.setAiResponse(aiReply);
        chatLog.setCreatedAt(LocalDateTime.now());

        chatLogRepository.save(chatLog);

        return new ChatResponseDTO(aiReply);
    }

}
