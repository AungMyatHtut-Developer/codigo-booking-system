package codigo.amh.codetest.bookingsystem.codigobookingsystem.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public BusinessException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}


