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
public class UserPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser user;

    @ManyToOne
    private Packages pack;

    private Integer remainingCredits;
    private Integer usedCredits;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
}