package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.ToString;

import static codigo.amh.codetest.bookingsystem.codigobookingsystem.util.MaskUtils.mask;

// Registration
public record RegistrationRequest(
        @NotBlank @Size(min=4, max=20) String username,
        @Email String email,
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$") String password,
        @NotBlank String countryCode
) {
    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + mask(password) + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}