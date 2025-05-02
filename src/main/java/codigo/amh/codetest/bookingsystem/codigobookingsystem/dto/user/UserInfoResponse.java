package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user;

import java.time.LocalDateTime;

public record UserInfoResponse(
        String username, LocalDateTime createdDate, String email, String countryCode
){
}
