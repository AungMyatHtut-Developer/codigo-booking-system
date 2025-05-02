package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequest(
        @Email String email
) {
}
