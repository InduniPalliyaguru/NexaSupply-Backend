package lk.ijse.NexaSupply.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private int status;
    private String message;

}
