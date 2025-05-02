package codigo.amh.codetest.bookingsystem.codigobookingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser user;

    @ManyToOne
    private Classes bookedClass;

    private LocalDateTime bookingDate;
    private Boolean attended;
    private Boolean cancelled;
    private boolean checkedIn;
    private LocalDateTime checkInTime;
}