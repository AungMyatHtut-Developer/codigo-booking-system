package codigo.amh.codetest.bookingsystem.codigobookingsystem.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, "CONFLICT", message);
    }
}