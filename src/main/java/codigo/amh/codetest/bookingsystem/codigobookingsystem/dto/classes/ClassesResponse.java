package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.classes;

public record ClassesResponse(
        Long id,
        String name,
        String countryCode,
        String startTime,
        String endTime,
        Integer creditRequired,
        Integer maxCapacity,
        String createdDate
) {
}
