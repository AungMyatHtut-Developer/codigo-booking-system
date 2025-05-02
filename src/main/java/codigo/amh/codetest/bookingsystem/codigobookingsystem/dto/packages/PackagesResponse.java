package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.packages;

import java.math.BigDecimal;

public record PackagesResponse(
        Long id,
        String name,
        String countryCode,
        Integer credits,
        BigDecimal price,
        Integer validDays,
        String createdDate
) {
}
