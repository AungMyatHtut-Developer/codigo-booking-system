package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

import jakarta.validation.constraints.Email;

public record GenerateOTPRequest(
        @Email
        String email
) {
}
