package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static codigo.amh.codetest.bookingsystem.codigobookingsystem.util.MaskUtils.mask;

public record PasswordChangeRequest(
        @NotBlank
        String currentPassword,
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")
        String newPassword,
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")
        String confirmPassword
) {
    @Override
    public String toString() {
        return "PasswordChangeRequest{" +
                "currentPassword='" + mask(currentPassword) + '\'' +
                ", newPassword='" + mask(newPassword) + '\'' +
                ", confirmPassword='" + mask(confirmPassword) + '\'' +
                '}';
    }
}
