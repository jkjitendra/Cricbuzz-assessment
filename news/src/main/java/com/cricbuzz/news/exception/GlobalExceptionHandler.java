package com.cricbuzz.news.exception;

import com.cricbuzz.news.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Void>> resourceNotFoundException(ResourceNotFoundException exception) {
        String message = exception.getMessage();
        APIResponse<Void> apiResponse = new APIResponse<>(false, message);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleMethodArgsNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> messageMap = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String defaultMessage = error.getDefaultMessage();
            messageMap.put(fieldName, defaultMessage);
        });
        APIResponse<Map<String, String>> apiResponse = new APIResponse<>(false, "Validation failed", messageMap);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        APIResponse<String> apiResponse = new APIResponse<>(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistingException.class)
    public ResponseEntity<APIResponse<String>> handleUserAlreadyExistingException(UserAlreadyExistingException ex) {
        APIResponse<String> apiResponse = new APIResponse<>(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIResponse<String>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        APIResponse<String> apiResponse = new APIResponse<>(false, "Endpoint not found: " + ex.getRequestURL());
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<APIResponse<String>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        APIResponse<String> apiResponse = new APIResponse<>(false, "File size exceeds the maximum allowed size.");
        return new ResponseEntity<>(apiResponse, HttpStatus.EXPECTATION_FAILED);
    }

}
