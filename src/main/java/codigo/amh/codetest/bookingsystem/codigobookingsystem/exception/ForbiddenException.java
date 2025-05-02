package codigo.amh.codetest.bookingsystem.codigobookingsystem.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }
}