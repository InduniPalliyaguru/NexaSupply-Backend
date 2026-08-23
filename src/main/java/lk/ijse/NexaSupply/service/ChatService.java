package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.chatbot.ChatRequestDTO;
import lk.ijse.NexaSupply.dto.chatbot.ChatResponseDTO;
import lk.ijse.NexaSupply.enumeration.Role;

public interface ChatService {

    ChatResponseDTO processChat(ChatRequestDTO requestDTO, String userEmail, Role userRole);

}
