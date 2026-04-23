package com.authunidate.exception;

import com.authunidate.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalException {
    private static final String MIN_STRING = "min";

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorException(AuthorizationDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleArgumentException(MethodArgumentNotValidException ex) {
        String enumKey = ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage() : null;
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            if (enumKey != null) {
                errorCode = ErrorCode.valueOf(enumKey);
                var constraintViolation = ex.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);
                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            }
        } catch (Exception ignored) {
        }
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes) ? mapAttribute(errorCode.getMessage(), attributes) : errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonFormatError(HttpMessageNotReadableException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(ErrorCode.DATE_FORMAT_INVALID.getCode())
                .message(ErrorCode.DATE_FORMAT_INVALID.getMessage())
                .build();
        return ResponseEntity.status(ErrorCode.DATE_FORMAT_INVALID.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(ErrorCode.USER_EXISTED.getCode())
                .message(ErrorCode.USER_EXISTED.getMessage())
                .build();
        return ResponseEntity.status(ErrorCode.USER_EXISTED.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder().code(404).message("Resource not found").build();
        return ResponseEntity.status(404).body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED.getMessage());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED.getHttpStatusCode()).body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String min = String.valueOf(attributes.get(MIN_STRING));
        return message.replace("{" + MIN_STRING + "}", min);
    }
}
