package by.nikiforova.epic_auth_service.exception;

import by.nikiforova.epic_auth_service.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String TIMEZONE = "Europe/Minsk";

    @ExceptionHandler(CredentialAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            CredentialAlreadyExistsException e, HttpServletRequest request) {
        log.info(e.getMessage());
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(ZoneId.of(TIMEZONE)),
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler ({
        InvalidCredentialsException.class,
                InvalidTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            RuntimeException e, HttpServletRequest request) {
        log.info(e.getMessage());
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(ZoneId.of(TIMEZONE)),
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            String message = error.getDefaultMessage();
            errors.put(error.getField(), message);
        }

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(ZoneId.of(TIMEZONE)),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedExceptions(Exception e, HttpServletRequest request) {
        log.error("Unexpected exception: {}", e.getMessage(), e);
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(ZoneId.of(TIMEZONE)),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}