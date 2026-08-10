package lk.ijse.NexaSupply.exception;

import lk.ijse.NexaSupply.constant.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<CommonResponse> handleServerException(Exception exception, WebRequest request) {
        log.error("Unexpected Server Error Occurred: ", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CommonResponse(500, "UNEXPECTED_ERROR"));
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<CommonResponse> handleCustomException(CustomException exception, WebRequest request) {
        log.warn("CustomException Occurred | Status: {} | Message: {}", exception.getStatus(), exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(new CommonResponse(exception.getStatus(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handleValidationException(MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = (fieldError != null) ? fieldError.getDefaultMessage() : "VALIDATION_FAILED";

        log.warn("Validation Failed | Field: {} | Message: {}",
                fieldError != null ? fieldError.getField() : "N/A", errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

}
