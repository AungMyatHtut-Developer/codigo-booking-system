package codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.classes;

public record BookedClassResponse(
        Long bookedClassId,
        String bookedClassName,
        String bookedClassStartTime,
        String bookedClassEndTime,
        Integer bookedClassRequiredCredit,
        Integer maxCapacity,
        String bookedClassCreatedDate,
        Long bookingId,
        String bookingDate,
        boolean attended,
        boolean cancelled
) {
}
