package lk.ijse.NexaSupply.exception;

import lk.ijse.NexaSupply.constant.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public CommonResponse handleServerException(Exception exception, WebRequest request) {
        exception.printStackTrace();
        return new CommonResponse(500, "UNEXPECTED_ERROR");
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<CommonResponse> handleCustomException(CustomException exception, WebRequest request) {
        exception.printStackTrace();
        return ResponseEntity
                .status(exception.getStatus())
                .body(new CommonResponse(exception.getStatus(), exception.getMessage()));
    }

}
