package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static codigo.amh.codetest.bookingsystem.codigobookingsystem.util.MaskUtils.mask;

// Verification
public record VerificationRequest(
        @Email String email,
        @NotBlank String otp
) {
    @Override
    public String toString() {
        return "VerificationRequest{" +
                "email='" + email + '\'' +
                ", otp='" + mask(otp) + '\'' +
                '}';
    }
}