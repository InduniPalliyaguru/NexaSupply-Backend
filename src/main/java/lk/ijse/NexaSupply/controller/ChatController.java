package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.chatbot.ChatRequestDTO;
import lk.ijse.NexaSupply.dto.chatbot.ChatResponseDTO;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RETAILER')")
    public CommonResponse askChatBot(@Valid @RequestBody ChatRequestDTO requestDTO, Authentication authentication) {
        String userEmail = authentication.getName();

        Role userRole = Role.ROLE_RETAILER;

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            userRole = Role.valueOf(authority.getAuthority());
            break;
        }

        ChatResponseDTO response = chatService.processChat(requestDTO, userEmail, userRole);

        return new CommonResponse(200, response, "Success");
    }

}
