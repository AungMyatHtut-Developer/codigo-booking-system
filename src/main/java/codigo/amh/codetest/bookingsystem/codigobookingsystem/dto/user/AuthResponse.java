package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

public record AuthResponse(String accessToken, String tokenType) {
    public AuthResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
