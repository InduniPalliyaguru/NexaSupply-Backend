package lk.ijse.NexaSupply.dto.chatbot;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {

    @NotBlank(message = "Message cannot be empty")
    private String message;

}
