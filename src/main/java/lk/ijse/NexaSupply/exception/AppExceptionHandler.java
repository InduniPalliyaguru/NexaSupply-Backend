package lk.ijse.NexaSupply.exception;

import lk.ijse.NexaSupply.constant.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public CommonResponse handleServerException(Exception exception, WebRequest request) {
        log.error("Unexpected Server Error Occurred: ", exception);
        return new CommonResponse(500, "UNEXPECTED_ERROR");
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<CommonResponse> handleCustomException(CustomException exception, WebRequest request) {
        log.warn("CustomException Occurred | Status: {} | Message: {}", exception.getStatus(), exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(new CommonResponse(exception.getStatus(), exception.getMessage()));
    }

}
