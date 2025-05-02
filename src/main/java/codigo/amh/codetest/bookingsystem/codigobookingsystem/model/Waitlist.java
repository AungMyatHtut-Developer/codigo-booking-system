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
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser user;

    @ManyToOne
    private Classes waitlistedClass;

    private LocalDateTime joinedAt;
    private Boolean notified; // to indicate if user was informed of an opening
}