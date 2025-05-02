package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static codigo.amh.codetest.bookingsystem.codigobookingsystem.util.MaskUtils.mask;

public record LoginRequest(
        @Email String email,
        @NotBlank String password
) {
    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='" + mask(password) + '\'' +
                '}';
    }
}