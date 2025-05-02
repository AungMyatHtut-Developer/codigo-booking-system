package codigo.amh.codetest.bookingsystem.codigobookingsystem.exception;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.ApiResponse;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        Map<String, String> errors = Map.of("code", ex.getErrorCode(), "message", ex.getMessage());
        return ResponseHelper.createResponse(ex.getStatus(), null, errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                                .orElse("Validation error")
                ));
        return ResponseHelper.createResponse(HttpStatus.BAD_REQUEST, null, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        logger.error("Internal server error", ex);
        return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                null, Map.of("message", "Internal server error"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Not found: " + ex.getResourcePath());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMethodNotSupported(NoResourceFoundException ex) {
        String errorMessage = "HTTP method '" + ex.getHttpMethod() + "' is not supported for this endpoint";
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorMessage);
    }
}
