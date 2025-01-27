package org.anonymous.global.advices;

import lombok.RequiredArgsConstructor;
import org.anonymous.global.exceptions.CommonException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class, basePackages = "org.anonymous")
public class CommonControllerAdvice {

    private final Utils utils;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONData> errorHandler(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본 에러 코드 500

        Object message = e.getMessage();

        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();

            Map<String, List<String>> errorMessages = commonException.getErrorMessages();
            if (errorMessages != null) {
                message = errorMessages;
            } else {
                message = commonException.isErrorCode() ? utils.getMessage((String)message) : message;
            }
        }

        JSONData data = new JSONData();
        data.setSuccess(false);
        data.setStatus(status);
        data.setMessage(message);

        e.printStackTrace();

        return ResponseEntity.status(status).body(data);
    }
}
